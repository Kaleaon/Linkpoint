# Linkpoint PWA - Deployment Instructions

This document provides step-by-step instructions for deploying the Linkpoint PWA to Vercel.

## Prerequisites

- Node.js 14.0.0 or higher
- Vercel account (free tier works)
- Git repository access

## Quick Start

### Option 1: Automatic Deployment via Vercel CLI

1. **Install Vercel CLI**
   ```bash
   npm install -g vercel
   ```

2. **Navigate to PWA directory**
   ```bash
   cd PWA-demo
   ```

3. **Login to Vercel**
   ```bash
   vercel login
   ```

4. **Deploy**
   ```bash
   # Deploy to preview
   vercel
   
   # Or deploy to production
   vercel --prod
   ```

### Option 2: GitHub Integration (Recommended)

1. **Push code to GitHub**
   ```bash
   git add .
   git commit -m "Setup Vercel deployment"
   git push origin main
   ```

2. **Connect to Vercel**
   - Go to [vercel.com](https://vercel.com)
   - Click "New Project"
   - Import your GitHub repository
   - Select the `PWA-demo` directory as the root
   - Click "Deploy"

3. **Automatic deployments**
   - Every push to main branch auto-deploys to production
   - Pull requests create preview deployments

## Verification Before Deployment

Before deploying, run the verification script to ensure all files are present:

```bash
cd PWA-demo
./verify-build.sh
```

This will check:
- All required files exist
- JSON configuration is valid
- All referenced assets are available
- Vercel configuration is correct

## Build Configuration

### Vercel Settings

The following are pre-configured in `vercel.json`:

- **Framework**: Static (no build required)
- **Output Directory**: `.` (current directory)
- **Build Command**: None needed
- **Install Command**: None needed

### Environment Variables

If you need to configure environment variables:

1. Via Vercel Dashboard:
   - Project Settings → Environment Variables
   - Add your variables
   - Redeploy

2. Via CLI:
   ```bash
   vercel env add VARIABLE_NAME
   ```

## Post-Deployment

### 1. Verify Deployment

After deployment, verify the following:

- [ ] URL loads without errors
- [ ] HTTPS is enabled (automatic on Vercel)
- [ ] Service Worker registers successfully
- [ ] PWA install prompt appears on mobile
- [ ] All assets load correctly
- [ ] 3D graphics render properly

### 2. Test PWA Features

Open Chrome DevTools and check:

1. **Application → Manifest**
   - Manifest should be loaded
   - Icons should be visible
   
2. **Application → Service Workers**
   - Service Worker should be active
   - Check "Update on reload" during testing

3. **Application → Cache Storage**
   - Static assets should be cached
   - Cache version should match service-worker.js

4. **Network → Offline**
   - Enable offline mode
   - App should still work

### 3. Run Lighthouse Audit

1. Open Chrome DevTools
2. Go to Lighthouse tab
3. Run audit with all categories
4. Expected scores:
   - Performance: 95+
   - Accessibility: 90+
   - Best Practices: 95+
   - SEO: 90+
   - PWA: 100

### 4. Test on Multiple Devices

Test on:
- Desktop browsers (Chrome, Firefox, Safari, Edge)
- Mobile browsers (iOS Safari, Chrome Android)
- Different screen sizes and orientations

## Troubleshooting

### Service Worker Not Registering

**Symptoms**: Console error about service worker registration

**Solutions**:
1. Verify HTTPS is enabled (Vercel provides this automatically)
2. Check browser console for specific errors
3. Clear cache and hard reload (Ctrl+Shift+R)
4. Check service-worker.js is accessible at root URL

### PWA Not Installable

**Symptoms**: Install prompt doesn't appear

**Solutions**:
1. Verify manifest.json is valid and accessible
2. Check that icons are properly sized (512x512 required)
3. Ensure start_url in manifest is correct
4. Test on different browsers (install criteria varies)

### Assets Not Loading

**Symptoms**: 404 errors for JS/CSS/images

**Solutions**:
1. Verify all paths in index.html are relative
2. Check vercel.json routes configuration
3. Ensure files are committed to git
4. Check .gitignore doesn't exclude needed files

### Build Fails on Vercel

**Symptoms**: Deployment fails with error

**Solutions**:
1. Check Vercel build logs for specific error
2. Run `verify-build.sh` locally to catch issues early
3. Ensure package.json has correct "engines" field
4. Verify vercel.json syntax is valid JSON

## Custom Domain

To add a custom domain:

1. **Via Vercel Dashboard**
   - Project Settings → Domains
   - Add your domain
   - Configure DNS as shown

2. **Via CLI**
   ```bash
   vercel domains add yourdomain.com
   ```

3. **DNS Configuration**
   ```
   Type: CNAME
   Name: www (or @)
   Value: cname.vercel-dns.com
   ```

## Rollback

If you need to rollback to a previous deployment:

1. **Via Dashboard**
   - Go to Deployments
   - Find previous deployment
   - Click "Promote to Production"

2. **Via CLI**
   ```bash
   vercel ls
   vercel promote <deployment-url>
   ```

## Performance Optimization

The following optimizations are already configured:

- **Static Asset Caching**: 1 year cache for JS/CSS/images
- **HTML No-Cache**: Always fetch fresh HTML
- **Service Worker Caching**: Offline support
- **Compression**: Automatic Gzip/Brotli
- **HTTP/2**: Enabled by default
- **CDN**: 100+ edge locations worldwide

## Security Headers

Pre-configured security headers in vercel.json:

- Content-Security-Policy
- X-Frame-Options: DENY
- X-Content-Type-Options: nosniff
- X-XSS-Protection
- Strict-Transport-Security
- Referrer-Policy
- Permissions-Policy

## Monitoring

### Vercel Analytics (Optional)

Enable analytics for performance monitoring:

```bash
vercel analytics
```

View in dashboard: Project → Analytics

### Custom Monitoring

Add performance tracking in your app:

```javascript
window.addEventListener('load', () => {
  const perfData = performance.getEntriesByType('navigation')[0];
  const loadTime = perfData.loadEventEnd - perfData.fetchStart;
  console.log('Page load time:', loadTime, 'ms');
});
```

## Continuous Deployment

### Automatic Deployments

With GitHub integration:
- Production: Pushes to `main` branch
- Preview: Pull requests and other branches

### Manual Deployments

```bash
# Preview deployment
vercel

# Production deployment
vercel --prod

# Deploy specific branch
vercel --prod --force
```

## Support

For issues related to:

- **PWA functionality**: Check browser console and Application tab
- **Vercel deployment**: Check [Vercel documentation](https://vercel.com/docs)
- **Build errors**: Run `verify-build.sh` for diagnostics

## Deployment Checklist

Before deploying to production:

- [ ] Run `verify-build.sh` and confirm all checks pass
- [ ] Test locally with production build
- [ ] Verify all links are relative
- [ ] Check service worker caching strategy
- [ ] Test offline functionality
- [ ] Run Lighthouse audit
- [ ] Check mobile responsiveness
- [ ] Verify 3D graphics work
- [ ] Test on multiple browsers
- [ ] Review security headers
- [ ] Check browser console for errors

After deploying:

- [ ] Verify deployment URL works
- [ ] Test PWA installation
- [ ] Check service worker registration
- [ ] Test offline mode
- [ ] Verify 3D graphics render
- [ ] Test on mobile devices
- [ ] Run Lighthouse on production URL
- [ ] Monitor for errors in first 24 hours

## Success Criteria

Your deployment is successful when:

1. ✅ URL loads without errors
2. ✅ HTTPS lock icon appears in browser
3. ✅ Install prompt shows on mobile
4. ✅ Service worker registers successfully
5. ✅ App works offline after first load
6. ✅ 3D world viewer renders correctly
7. ✅ All navigation and features work
8. ✅ Lighthouse PWA score: 100
9. ✅ No console errors or warnings
10. ✅ Fast load times (<3s first load, <1s cached)

---

## Quick Reference

```bash
# Install CLI
npm install -g vercel

# Deploy preview
cd PWA-demo && vercel

# Deploy production
cd PWA-demo && vercel --prod

# Verify before deploy
cd PWA-demo && ./verify-build.sh

# View logs
vercel logs

# List deployments
vercel ls
```

---

**Ready to deploy!** 🚀

For the most up-to-date deployment guide, see [VERCEL_DEPLOYMENT.md](VERCEL_DEPLOYMENT.md).
