// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '../views/HomePage.vue';
import VisualizationPage from '../views/VisualizationPage.vue';
import AttributeQuery from "@/views/AttributeQuery.vue";
import RelationQuery from "@/views/RelationQuery.vue";
import CustomQuery from "@/views/Neo4jBroswer.vue";
import DownloadPage from "@/views/DownloadPage.vue";
import HelpPage from "@/views/HelpPage.vue";
import NodeDetails from "@/views/NodeDetails.vue";
import GODistribution from "@/views/GoDistribution.vue";
import PathwayDistribution from "@/views/PathwayDistribution.vue";
import EnrichmentNetwork from "@/views/EnrichmentNetwork.vue";
import ChatBot from "@/views/ChatBot.vue";


const routes = [
  {
    path: '/',
    name: 'home',
    component: HomePage
  },
  {
    path: "/attribute-query",
    name: "attributeQuery",
    component: AttributeQuery,
  },
  {
    path: "/relation-query",
    name: "relationQuery",
    component: RelationQuery,
  },
  {
    path: '/visualization',
    name: 'visualization',
    component: VisualizationPage
  },
  {
    path: "/godistribution",
    name: "GODistribution",
    component: GODistribution,
  },
  {
    path: "/pathwaydistribution",
    name: "PathwayDistribution",
    component: PathwayDistribution,
  },
  {
    path: "/enrichmentnetwork",
    name: "EnrichmentNetwork",
    component: EnrichmentNetwork,
  },
  {
    path: '/custom-query',
    name: 'CustomQuery',
    component: CustomQuery
  },
  {
    path: '/download',
    name: 'Download',
    component: DownloadPage
  },
  {
    path: '/help',
    name: 'Help',
    component: HelpPage
  },
  {
    path: '/chatbot',
    name: 'ChatBot',
    component: ChatBot,
  },
  {
    path: "/node-details",
    name: "NodeDetails",
    component: NodeDetails,
  },
];

const router = createRouter({
  history: createWebHistory('/G4KG/'), // 使用 HTML5 History 模式
  routes, // 路由配置
});

export default router;
