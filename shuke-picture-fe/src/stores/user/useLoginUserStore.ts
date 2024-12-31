import { defineStore } from "pinia";
import { reactive } from "vue";

export const useLoginUserStore = defineStore("loginUser", () => {
  const loginUser = reactive<any>({
    userName: "未登录",
  });

  async function fetchLoginUser() {
    // 模拟用户登录
    setTimeout(()=>{
      loginUser.value = {userName:"路人甲", id:"1"}
    },3000)
  }

  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser;
  }

  return { loginUser, setLoginUser, fetchLoginUser };
});
