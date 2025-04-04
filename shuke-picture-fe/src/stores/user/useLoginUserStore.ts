import { defineStore } from "pinia";
import { ref } from "vue";
import { getLoginUserUsingGet }from "@/api/userController.ts"
export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<API.UserLoginVO>({
    userName: "未登录",
  });

  /**
   * 获取登录用户信息
   */
  async function fetchLoginUser() {
    const res = await getLoginUserUsingGet()
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data
    }
  }

  /**
   *
   * @param newLoginUser 设置登录用户
   */
  function setLoginUser(newLoginUser: any) {
      loginUser.value = newLoginUser;
  }

  return { loginUser, setLoginUser, fetchLoginUser };
});
