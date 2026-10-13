import type { Core } from '@strapi/strapi';

const PUBLIC_AUTH_ACTIONS = [
  'plugin::users-permissions.auth.callback', // POST /api/auth/local
  'plugin::users-permissions.auth.refresh', // POST /api/auth/refresh
] as const;

const AUTHENTICATED_ACTIONS = [
  'api::store.store.find',
  'api::store.store.findOne',
  'api::visit-report.visit-report.create',
  'api::visit-report.visit-report.find',
  'api::visit-report.visit-report.findOne',
  // Strapi 5: media must be uploaded first, then linked on create.
  'plugin::upload.content-api.upload',
] as const;

const SEED_STORES = [
  {
    name: 'Toko Sumber Rejeki',
    address: 'Jl. Merdeka No. 12, Bandung',
    owner_name: 'Budi Santoso',
  },
  {
    name: 'Warung Makmur Jaya',
    address: 'Jl. Asia Afrika No. 45, Bandung',
    owner_name: 'Siti Aminah',
  },
  {
    name: 'Minimarket Sejahtera',
    address: 'Jl. Sudirman No. 88, Cimahi',
    owner_name: 'Andri Wijaya',
  },
  {
    name: 'Toko Berkah Abadi',
    address: 'Jl. Cihampelas No. 21, Bandung',
    owner_name: 'Dewi Lestari',
  },
  {
    name: 'Depot Sembako Nusantara',
    address: 'Jl. Pasteur No. 7, Bandung',
    owner_name: 'Rudi Hartono',
  },
] as const;

const SEED_ADMIN = {
  email: 'admin@mail.test',
  password: 'admin123!',
  firstname: 'Admin',
  lastname: 'Canvassku',
} as const;

const SEED_APP_USER = {
  username: 'budi',
  email: 'budi@mail.test',
  password: 'sales123!',
} as const;

async function ensurePermissions(
  strapi: Core.Strapi,
  roleType: 'public' | 'authenticated',
  actions: readonly string[]
) {
  const role = await strapi.db.query('plugin::users-permissions.role').findOne({
    where: { type: roleType },
  });

  if (!role) {
    strapi.log.warn(`Role "${roleType}" not found; skipping permission setup.`);
    return;
  }

  for (const action of actions) {
    const existing = await strapi.db.query('plugin::users-permissions.permission').findOne({
      where: { action, role: role.id },
    });

    if (!existing) {
      await strapi.db.query('plugin::users-permissions.permission').create({
        data: { action, role: role.id },
      });
      strapi.log.info(`Enabled permission ${action} for role "${roleType}".`);
    }
  }
}

async function seedStores(strapi: Core.Strapi) {
  const existingCount = await strapi.db.query('api::store.store').count();
  if (existingCount > 0) {
    strapi.log.info(`Stores already seeded (${existingCount}); skipping.`);
    return;
  }

  for (const store of SEED_STORES) {
    await strapi.documents('api::store.store').create({ data: { ...store } });
  }

  strapi.log.info(`Seeded ${SEED_STORES.length} dummy stores.`);
}

async function seedAdmin(strapi: Core.Strapi) {
  const existing = await strapi.db.query('admin::user').findOne({
    where: { email: SEED_ADMIN.email },
  });
  if (existing) {
    strapi.log.info(`Admin already seeded (${SEED_ADMIN.email}); skipping.`);
    return;
  }

  const superAdminRole = await strapi.db.query('admin::role').findOne({
    where: { code: 'strapi-super-admin' },
  });
  if (!superAdminRole) {
    strapi.log.warn('Super admin role not found; skipping admin seed.');
    return;
  }

  await strapi.admin.services.user.create({
    email: SEED_ADMIN.email,
    firstname: SEED_ADMIN.firstname,
    lastname: SEED_ADMIN.lastname,
    password: SEED_ADMIN.password,
    registrationToken: null,
    isActive: true,
    roles: [superAdminRole.id],
  });

  strapi.log.info(`Seeded admin ${SEED_ADMIN.email}.`);
}

async function seedAppUser(strapi: Core.Strapi) {
  const existing = await strapi.db.query('plugin::users-permissions.user').findOne({
    where: { email: SEED_APP_USER.email },
  });
  if (existing) {
    strapi.log.info(`App user already seeded (${SEED_APP_USER.email}); skipping.`);
    return;
  }

  const authenticatedRole = await strapi.db.query('plugin::users-permissions.role').findOne({
    where: { type: 'authenticated' },
  });
  if (!authenticatedRole) {
    strapi.log.warn('Authenticated role not found; skipping app user seed.');
    return;
  }

  await strapi.plugin('users-permissions').service('user').add({
    username: SEED_APP_USER.username,
    email: SEED_APP_USER.email,
    password: SEED_APP_USER.password,
    confirmed: true,
    blocked: false,
    provider: 'local',
    role: authenticatedRole.id,
  });

  strapi.log.info(`Seeded app user ${SEED_APP_USER.email}.`);
}

export default {
  register(/* { strapi }: { strapi: Core.Strapi } */) {},

  async bootstrap({ strapi }: { strapi: Core.Strapi }) {
    await ensurePermissions(strapi, 'public', PUBLIC_AUTH_ACTIONS);
    await ensurePermissions(strapi, 'authenticated', AUTHENTICATED_ACTIONS);
    await seedAdmin(strapi);
    await seedAppUser(strapi);
    await seedStores(strapi);
  },
};
