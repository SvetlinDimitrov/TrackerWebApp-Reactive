<template>
  <CustomFoodCreation
      header="Create Custom Food"
      @submit="submit"
      @close="handleClose"/>
</template>

<script setup>
import router from "../../router/index.js";
import {useStore} from "vuex";
import {useToast} from "primevue/usetoast"
import CustomFoodCreation from "../../components/mealFood/CustomFoodCreation.vue";

const toast = useToast();
const store = useStore();

const submit = async (mealFood) => {
  if (mealFood.foodDetails) {
    mealFood.foodDetails.info = mealFood.foodDetails.info === "" ? null : mealFood.foodDetails.info;
    mealFood.foodDetails.largeInfo = mealFood.foodDetails.largeInfo === "" ? null : mealFood.foodDetails.largeInfo;
    mealFood.foodDetails.picture = mealFood.foodDetails.picture === "" ? null : mealFood.foodDetails.picture;
  }
  try {
    await store.dispatch('createCustomFood', mealFood);
    toast.add({severity: 'success', summary: 'Success', detail: 'Food created successfully' , life:3000});
    router.go(-1);
  } catch (e) {
    toast.add({severity: 'error', summary: 'Error', detail: "Food creation failed" , life: 3000});
  }
};

const handleClose = () => {
  router.go(-1);
};
</script>