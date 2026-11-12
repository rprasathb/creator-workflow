import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ContentItem, ContentMetrics, ContentPayload, ContentStatus, DashboardSummary, MetricsPayload, Page, Platform } from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  private readonly base = '/api/v1';

  list(filters: { platform?: Platform | ''; status?: ContentStatus | ''; search?: string; page?: number }) {
    let params = new HttpParams().set('page', filters.page ?? 0).set('size', 20);
    if (filters.platform) params = params.set('platform', filters.platform);
    if (filters.status) params = params.set('status', filters.status);
    if (filters.search?.trim()) params = params.set('search', filters.search.trim());
    return this.http.get<Page<ContentItem>>(`${this.base}/content`, { params });
  }
  create(payload: ContentPayload) {
    return this.http.post<ContentItem>(`${this.base}/content`, payload);
  }
  update(id: string, payload: ContentPayload) {
    return this.http.put<ContentItem>(`${this.base}/content/${id}`, payload);
  }
  remove(id: string) {
    return this.http.delete<void>(`${this.base}/content/${id}`);
  }
  restore(id: string) {
    return this.http.post<ContentItem>(`${this.base}/content/${id}/restore`, {});
  }
  saveMetrics(id: string, payload: MetricsPayload) {
    return this.http.put<ContentMetrics>(`${this.base}/content/${id}/metrics`, payload);
  }
  summary() {
    return this.http.get<DashboardSummary>(`${this.base}/dashboard/summary`);
  }
  upcoming() {
    return this.http.get<ContentItem[]>(`${this.base}/dashboard/upcoming`);
  }
}
