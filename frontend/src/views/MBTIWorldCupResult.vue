<template>
  <el-container style="background-color: #fadce1">
    <el-header>
      <img
        class="logo"
        @click="goHome"
        src="@/assets/logo.png"
        alt="logo"
        style="cursor: pointer"
      />
    </el-header>
    <el-card
      style="
        text-align: center;
        background-color: #fadce1;
        box-shadow: none;
        border: none;
      "
    >
      <div>
        <img
          class="small"
          style="width: 250px; height: 250px"
          src="@/assets/smallpurple.png"
        />
        <span class="result"
          >MBTI 월드컵 우승자는
          <span style="color: rgb(255, 91, 136)">
            {{ $route.params.mbti }}
          </span>
          입니다!</span
        >
        <img class="small" src="@/assets/smallorange.png" />
      </div>
      <el-footer style="margin-top: 50px">
        <el-button type="danger" plain size="large" @click="goHome" round
          >홈으로</el-button
        >
      </el-footer>
    </el-card>
  </el-container>
</template>

<script>
import { useRouter } from "vue-router";
export default {
  setup() {
    const router = useRouter();
    const goHome = function () {
      router.push({ name: "HomeView" });
    };
    return { goHome };
  },
};
</script>

<style>
.small {
  width: 200px;
  height: 250px;
  vertical-align: bottom;
}

.result {
  display: inline-block;
  text-align: center;
  font-weight: bold;
  font-size: xx-large;
  height: 275px;
  line-height: 275px;
  width: 600px;
  border: 20px solid rgb(255, 91, 136);
  border-radius: 1rem;
  background-color: white;
  box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.12);
  font-family: "Dalseo";
}
</style>
