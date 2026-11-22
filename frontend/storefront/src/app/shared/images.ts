// Most seed products have no image yet: use a stable placeholder picture based on the slug.
export function imageFor(slug?: string, url?: string | null): string{
    return url ?? `https://picsum.photos/seed/${slug ?? 'shopverse'}/600/400`;
}