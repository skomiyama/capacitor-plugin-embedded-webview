<template>
  <div id="container">
    <strong>{{ name }}</strong>
    <p>Explore <a target="_blank" rel="noopener noreferrer" href="https://ionicframework.com/docs/components">UI Components</a></p>
    <ion-button @click="pushToEmbedPage($el ,'/tabs/tab2')">Push to second page</ion-button>
  </div>
</template>

<script lang="ts">
import { useIonRouter } from '@ionic/vue';
import { EmbeddedWebView } from '@skomiyama/embedded-webview';
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'ExploreContainer',
  props: {
    name: String,
  },
  setup() {
    const router = useIonRouter();
    return {
      pushToEmbedPage: async (el: HTMLElement, path: string) => {
        await EmbeddedWebView.pushTo({ path: '/second' });
        router.replace(path);
      }
    }
  }
});
</script>

<style scoped>
#container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

#container strong {
  font-size: 20px;
  line-height: 26px;
}

#container p {
  font-size: 16px;
  line-height: 22px;
  color: #8c8c8c;
  margin: 0;
}

#container a {
  text-decoration: none;
}
</style>
