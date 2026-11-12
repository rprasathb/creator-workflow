import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiService } from './api.service';
import { toIso, toLocalInput } from './date-utils';
import {
  ApiError, ContentItem, ContentPayload, ContentStatus, DashboardSummary,
  MetricsPayload, PLATFORM_NAMES, PLATFORMS, Platform, STATUSES
} from './models';

type View = 'dashboard' | 'content';
type FormModel = {
  title: string;
  description: string;
  platform: Platform;
  status: ContentStatus;
  scheduledAt: string;
  publishedAt: string;
  publishedUrl: string;
};
const emptyForm = (): FormModel => ({
  title: '', description: '', platform: 'TIKTOK', status: 'IDEA',
  scheduledAt: '', publishedAt: '', publishedUrl: ''
});

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  private readonly api = inject(ApiService);
  readonly statuses = STATUSES;
  readonly platforms = PLATFORMS;
  readonly platformNames = PLATFORM_NAMES;
  view: View = 'dashboard';
  items: ContentItem[] = [];
  upcoming: ContentItem[] = [];
  summary: DashboardSummary | null = null;
  total = 0;
  page = 0;
  totalPages = 0;
  loading = false;
  saving = false;
  error = '';
  notice = '';
  search = '';
  platformFilter: Platform | '' = '';
  statusFilter: ContentStatus | '' = '';
  showEditor = false;
  editingId: string | null = null;
  form: FormModel = emptyForm();
  metricsTarget: ContentItem | null = null;
  metrics: MetricsPayload = { views: 0, likes: 0, comments: 0, shares: 0 };

  ngOnInit(): void { this.refresh(); }

  switchView(view: View): void {
    this.view = view;
    this.error = '';
    this.notice = '';
    this.refresh();
  }

  refresh(): void {
    if (this.view === 'dashboard') this.loadDashboard();
    else this.loadContent();
  }

  loadDashboard(): void {
    this.loading = true;
    this.error = '';
    this.api.summary().subscribe({
      next: data => {
        this.summary = data;
        this.api.upcoming().subscribe({
          next: upcoming => { this.upcoming = upcoming; this.loading = false; },
          error: error => { this.loading = false; this.error = this.describeError(error); }
        });
      },
      error: error => { this.loading = false; this.error = this.describeError(error); }
    });
  }

  loadContent(): void {
    this.loading = true;
    this.error = '';
    this.api.list({ platform: this.platformFilter, status: this.statusFilter, search: this.search, page: this.page })
      .subscribe({
        next: result => {
          this.items = result.content;
          this.total = result.totalElements;
          this.totalPages = result.totalPages;
          this.loading = false;
        },
        error: error => { this.loading = false; this.error = this.describeError(error); }
      });
  }

  applyFilters(): void { this.page = 0; this.loadContent(); }
  movePage(delta: number): void {
    const next = this.page + delta;
    if (next >= 0 && next < this.totalPages) { this.page = next; this.loadContent(); }
  }

  openNew(): void {
    this.error = '';
    this.editingId = null;
    this.form = emptyForm();
    this.showEditor = true;
  }
  openEdit(item: ContentItem): void {
    this.error = '';
    this.editingId = item.id;
    this.form = {
      title: item.title,
      description: item.description ?? '',
      platform: item.platform,
      status: item.status,
      scheduledAt: toLocalInput(item.scheduledAt),
      publishedAt: toLocalInput(item.publishedAt),
      publishedUrl: item.publishedUrl ?? ''
    };
    this.showEditor = true;
  }
  closeEditor(): void { this.showEditor = false; this.error = ''; }

  saveContent(): void {
    if (this.saving) return;
    this.error = '';
    if (!this.form.title.trim()) { this.error = 'Please enter a content title.'; return; }
    if (this.form.status === 'SCHEDULED' && !this.form.scheduledAt) {
      this.error = 'A planned publish date is required for scheduled content.'; return;
    }
    if (this.form.status === 'PUBLISHED' && !this.form.publishedAt) {
      this.error = 'An actual publication date is required for published content.'; return;
    }
    const payload: ContentPayload = {
      title: this.form.title.trim(),
      description: this.form.description.trim() || null,
      platform: this.form.platform,
      status: this.form.status,
      scheduledAt: toIso(this.form.scheduledAt),
      publishedAt: toIso(this.form.publishedAt),
      publishedUrl: this.form.publishedUrl.trim() || null
    };
    this.saving = true;
    const operation = this.editingId ? this.api.update(this.editingId, payload) : this.api.create(payload);
    operation.subscribe({
      next: () => {
        this.saving = false; this.showEditor = false;
        this.notice = this.editingId ? 'Content updated.' : 'Content created.';
        this.view = 'content'; this.loadContent();
      },
      error: error => { this.saving = false; this.error = this.describeError(error); }
    });
  }

  deleteContent(item: ContentItem): void {
    if (!confirm(`Delete "${item.title}"? This cannot be undone.`)) return;
    this.error = '';
    this.api.remove(item.id).subscribe({
      next: () => { this.notice = 'Content deleted.'; this.loadContent(); },
      error: error => this.error = this.describeError(error)
    });
  }

  restoreContent(item: ContentItem): void {
    this.api.restore(item.id).subscribe({
      next: () => { this.notice = 'Content restored to ideas.'; this.loadContent(); },
      error: error => this.error = this.describeError(error)
    });
  }

  openMetrics(item: ContentItem): void {
    this.metricsTarget = item;
    this.metrics = { views: 0, likes: 0, comments: 0, shares: 0 };
    this.error = '';
  }
  closeMetrics(): void { this.metricsTarget = null; this.error = ''; }
  saveMetrics(): void {
    if (!this.metricsTarget || this.saving) return;
    if (Object.values(this.metrics).some(value => !Number.isSafeInteger(value) || value < 0)) {
      this.error = 'Metrics must be non-negative whole numbers.'; return;
    }
    this.saving = true; this.error = '';
    this.api.saveMetrics(this.metricsTarget.id, this.metrics).subscribe({
      next: () => {
        this.saving = false; this.metricsTarget = null;
        this.notice = 'Metrics saved.'; this.refresh();
      },
      error: error => { this.saving = false; this.error = this.describeError(error); }
    });
  }

  count(status: ContentStatus): number { return this.summary?.byStatus?.[status] ?? 0; }
  statusLabel(status: ContentStatus): string { return status.toLowerCase().replace('_', ' '); }
  private describeError(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) return 'Cannot reach the backend. Start Spring Boot on port 8080 and try again.';
      const body = error.error as ApiError | undefined;
      const details = Array.isArray(body?.details) ? body.details.join(', ') : '';
      return [body?.message || `Request failed (HTTP ${error.status})`, details].filter(Boolean).join(': ');
    }
    return 'Something went wrong. Please try again.';
  }
}
