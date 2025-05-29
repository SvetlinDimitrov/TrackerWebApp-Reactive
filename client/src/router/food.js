export default [
    {
        path: '/meal/:id/insert-custom-food',
        name: 'CustomInsertFood',
        component: () => import('../views/food/CustomInsertFood.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/meal/:id/insert-custom-food/:foodId',
        name: 'CustomFood',
        component: () => import('../views/food/CustomFood.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/custom-food/creation',
        name: 'CreateCustomFood',
        component: () => import('../views/food/CreateCustomFood.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/custom-food/:foodId/edit',
        name: 'EditCustomFood',
        component: () => import('../views/food/EditCustomFood.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/meal/:id/insert-food',
        name: 'InsertFood',
        component: () => import('../views/food/InsertFood.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/meal/:id/insert-food/common/:name',
        name: 'CommonFood',
        component: () => import('../views/food/CommonFood.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/meal/:id/edit/food/:foodId',
        name: 'EditInsertedFood',
        component: () => import('../views/food/EditInsertedFood.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/meal/:id/remove/food/:foodId',
        name: 'RemoveFoodById',
        component: () => import('../views/food/RemoveFoodById.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/meal/:id/insert-food/branded/:foodId',
        name: 'BrandedFood',
        component: () => import('../views/food/BrandedFood.vue'),
    }
];