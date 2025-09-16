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
import {useToast} from "primevue/usetoast"
import {useStore} from "vuex";
import {useRoute} from "vue-router";
import Food from "../../components/mealFood/Food.vue";

const store = useStore();
const route = useRoute();
const toast = useToast();
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

  try {
    mealFood.value = await store.dispatch("getCustomFoodById", foodId.value);
    hardCopyOfCurrentFood.value = JSON.parse(JSON.stringify(mealFood.value));
  } catch (error) {
    await router.push({name: 'Home'});
    toast.add({severity: 'error', summary: 'Error', detail: error.message, life: 3000});
  }
});

const handleClose = () => {
  router.go(-1);
};

const handleSubmit = async (mealFood) => {
  const payload = {
    mealId: mealId.value,
    mealFood: mealFood
  };
  try {
    await store.dispatch("addFoodIntoMeal", payload);
    toast.add({severity: 'success', summary: 'Success', detail: 'Food added successfully', life: 3000});
    await router.push({name: 'Home'});
  } catch (error) {
    toast.add({severity: 'error', summary: 'Error', detail: error.message, life: 3000});
  }
};
</script>
