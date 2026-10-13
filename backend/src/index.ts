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

export default {
  register(/* { strapi }: { strapi: Core.Strapi } */) {},

  async bootstrap({ strapi }: { strapi: Core.Strapi }) {
    await ensurePermissions(strapi, 'public', PUBLIC_AUTH_ACTIONS);
    await ensurePermissions(strapi, 'authenticated', AUTHENTICATED_ACTIONS);
    await seedStores(strapi);
  },
};
