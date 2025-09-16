<template>
  <Food v-if="mealFood && hardCopyOfCurrentFood"
        :mealFood="mealFood"
        :originalFood="hardCopyOfCurrentFood"
        @close="handleClose"
        @submit="handleSubmit"/>
</template>

<script setup>
import {onMounted, ref} from 'vue';
import router from "../../router/index.js";
import {useStore} from "vuex";
import {useRoute} from "vue-router";
import Food from "../../components/mealFood/Food.vue";
import {useToast} from "primevue/usetoast";
import {generateGeneralFood} from "../../utils/mealFood.js";

const toast = useToast();
const store = useStore();
const route = useRoute();
const mealId = ref(route.params.id);
const foodId = ref(route.params.foodId);
const mealFood = ref(null);
const hardCopyOfCurrentFood = ref(null);

onMounted(async () => {
  const currentMeal = store.getters.meals[mealId.value];

  if (!currentMeal) {
    toast.add({severity: 'error', summary: 'Error', detail: 'no meal found', life: 3000});
    await router.push({name: 'Home'});
    return;
  }

  currentMeal.foods.forEach((f) => {
    if (f.id === foodId.value) {

      mealFood.value = generateGeneralFood(f);
      hardCopyOfCurrentFood.value = JSON.parse(JSON.stringify(mealFood.value)); // Deep copy
    }
  });

  if (mealFood.value === null) {
    toast.add({severity: 'error', summary: 'Error', detail: 'Food not found', life: 3000});
    await router.push({name: 'Home'});
  }
});

const handleClose = () => {
  router.push({name: 'Home'});
};

const handleSubmit = async (mealFood) => {
  try {
    await store.dispatch("changeFoodById", {mealId: mealId.value, foodId: foodId.value, mealFood});
    toast.add({severity: 'success', summary: 'Success', detail: 'Food changed successfully', life: 3000});
    await router.push({name: 'Home'});
  } catch (error) {
    toast.add({severity: 'error', summary: 'Error', detail: error.message, life: 3000});
  }
};
</script>

<style scoped>

</style>