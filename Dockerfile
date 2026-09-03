# ==============================================================================
# 🌾 KaamSetu Frontend — Production PWA Nginx Container
# Base: Nginx 1.25 Alpine Linux
# ==============================================================================

FROM nginx:1.25-alpine

# Set labels for metadata
LABEL maintainer="KaamSetu Core Team <admin@kaamsetu.org>"
LABEL description="KaamSetu Rural Local Jobs SaaS Platform — PWA Frontend Container"

# Remove default nginx static assets and configuration
RUN rm -rf /usr/share/nginx/html/* /etc/nginx/conf.d/default.conf

# Copy custom Nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy frontend static assets into Nginx web root
COPY index.html /usr/share/nginx/html/
COPY manifest.json /usr/share/nginx/html/
COPY service-worker.js /usr/share/nginx/html/
COPY css/ /usr/share/nginx/html/css/
COPY js/ /usr/share/nginx/html/js/

# Set proper file permissions for the nginx user
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html

# Expose HTTP port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost/ || exit 1

# Start Nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
