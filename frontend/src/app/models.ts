export type Platform = 'TIKTOK' | 'YOUTUBE_SHORTS' | 'INSTAGRAM_REELS';
export type ContentStatus = 'IDEA' | 'DRAFT' | 'READY' | 'SCHEDULED' | 'PUBLISHED' | 'ARCHIVED';
export const PLATFORMS: Platform[] = ['TIKTOK', 'YOUTUBE_SHORTS', 'INSTAGRAM_REELS'];
export const STATUSES: ContentStatus[] = ['IDEA', 'DRAFT', 'READY', 'SCHEDULED', 'PUBLISHED', 'ARCHIVED'];
export const PLATFORM_NAMES: Record<Platform, string> = {
  TIKTOK: 'TikTok', YOUTUBE_SHORTS: 'YouTube Shorts', INSTAGRAM_REELS: 'Instagram Reels'
};
export type ContentPayload = {
  title: string;
  description: string | null;
  platform: Platform;
  status: ContentStatus;
  scheduledAt: string | null;
  publishedAt: string | null;
  publishedUrl: string | null;
};
export type ContentItem = ContentPayload & {
  id: string;
  createdAt: string;
  updatedAt: string;
};
export type Page<T> = { content: T[]; totalElements: number; totalPages: number; number: number; size: number };
export type ContentMetrics = { views: number; likes: number; comments: number; shares: number; recordedAt: string };
export type MetricsPayload = Omit<ContentMetrics, 'recordedAt'>;
export type DashboardSummary = { byStatus: Record<ContentStatus, number>; totalViews: number; totalLikes: number; publishedCount: number };
export type ApiError = { code?: string; message?: string; details?: string[] };
