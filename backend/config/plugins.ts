import type { Core } from '@strapi/strapi';

const FIVE_MINUTES_SECONDS = 5 * 60;

const allowedMediaTypes = [
  'image/*',
  'video/*',
  'audio/*',
  'application/pdf',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.*',
  'text/plain',
  'text/csv',
];

const deniedTypes = [
  'image/svg+xml',
  'application/vnd.microsoft.portable-executable',
  'application/x-msdownload',
  'application/x-msdos-program',
  'application/x-executable',
  'application/x-dosexec',
  'application/x-sh',
  'text/x-shellscript',
  'application/x-mach-binary',
];

const config = ({ env }: Core.Config.Shared.ConfigParams): Core.Config.Plugin => ({
  'users-permissions': {
    config: {
      jwtManagement: 'refresh',
      jwt: {
        expiresIn: '5m',
      },
      sessions: {
        // Access JWT lifetime: 5 minutes (spec requirement).
        accessTokenLifespan: FIVE_MINUTES_SECONDS,
        maxRefreshTokenLifespan: 30 * 24 * 60 * 60, // 30 days
        idleRefreshTokenLifespan: 14 * 24 * 60 * 60, // 14 days
        // Mobile clients store refreshToken from the JSON body (DataStore).
        httpOnly: false,
      },
    },
  },
  upload: {
    config: {
      security: {
        allowedTypes: allowedMediaTypes,
        deniedTypes,
      },
    },
  },
});

export default config;
