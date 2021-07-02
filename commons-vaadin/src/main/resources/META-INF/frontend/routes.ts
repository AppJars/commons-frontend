import { Flow } from '@vaadin/flow-frontend/Flow';
import { Route } from '@vaadin/router';
import './views/main-layout';
import './views/welcome/welcome-view';
import './views/login/login-view';

export type ViewRoute = Route & { title?: string; children?: ViewRoute[] };

const { serverSideRoutes } = new Flow({
  imports: () => import('../frontend/generated-flow-imports')
});

export const views: ViewRoute[] = [
  // for client-side, place routes below (more info https://vaadin.com/docs/v18/flow/typescript/creating-routes.html)
  {
    path: '',
    component: 'login-view',
    title: 'Login',
  },
  {
    path: 'about',
    component: 'about-view',
    title: 'About',
    action: async () => {
      await import('./views/about/about-view');
    },
  },
];
export const routes: ViewRoute[] = [
  // this one is not using main-layout because it's rendered after logging in inside the iframe
  {
    path: 'welcome',
    component: 'welcome-view',
    title: 'Welcome',
    action: async () => {
      await import('./views/welcome/welcome-view');
    }
  },
  {
    path: '',
    component: 'main-layout',
    children: [
      ...views,
      // for server-side, the next magic line sends all unmatched routes:
      ...serverSideRoutes, // IMPORTANT: this must be the last entry in the array
    ],
  },
];
