<template>
  <CustomFoodCreation
      v-if="mealFood"
      header="Edit Custom Food"
      :mealFood="mealFood"
      @submit="handleSubmit"
      @close="handleClose"/>
</template>

<script setup>
import {onMounted, ref} from 'vue';
import router from "../../router/index.js";
import {useStore} from "vuex";
import {useRoute} from "vue-router";
import {useToast} from "primevue/usetoast";
import CustomFoodCreation from "../../components/mealFood/CustomFoodCreation.vue";

const toast = useToast();
const store = useStore();
const route = useRoute();
const foodId = ref(route.params.foodId);
const mealFood = ref(null);

onMounted(async () => {
  try {
    mealFood.value = await store.dispatch('getCustomFoodById', foodId.value);
  } catch (error) {
    toast.add({severity: 'error', summary: 'Error', detail: error.message, life: 3000});
    router.go(-1);
  }

  if (mealFood.value === null) {
    toast.add({severity: 'error', summary: 'Error', detail: 'Food not found', life: 3000});
    router.go(-1);
  }
});

const handleClose = () => {
  router.go(-1);
};

const handleSubmit = async (mealFood) => {
  try {
    if (mealFood.foodDetails) {
      mealFood.foodDetails.info = mealFood.foodDetails.info === "" ? null : mealFood.foodDetails.info;
      mealFood.foodDetails.largeInfo = mealFood.foodDetails.largeInfo === "" ? null : mealFood.foodDetails.largeInfo;
      mealFood.foodDetails.picture = mealFood.foodDetails.picture === "" ? null : mealFood.foodDetails.picture;
    }
    await store.dispatch('changeCustomFoodById', {mealFood, id: foodId.value});
    toast.add({severity: 'success', summary: 'Success', detail: 'Food updated successfully', life: 3000});
    router.go(-1);
  } catch (error) {
    toast.add({severity: 'error', summary: 'Error', detail: error.message, life: 3000});
    router.go(-1);
  }
};
</script>

<style scoped>

</style>