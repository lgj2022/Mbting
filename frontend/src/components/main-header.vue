<template>
  <div class="navbar">
    <div style="display: absolute">
      <img
        class="logo"
        @click="goHome"
        src="@/assets/logo.png"
        alt="logo"
        style="cursor: pointer"
      />
    </div>

    <el-dropdown v-if="state.memberinfo">
      <img class="el-dropdown-link" :src="state.memberinfo.profileUrl" />
      <template #dropdown>
        <el-dropdown-menu class="dropdown-css">
          <el-dropdown-item @click="goMyPage" :icon="Avatar"
            >마이페이지</el-dropdown-item
          >
          <el-dropdown-item @click="goPeople" :icon="Comment">
            피플
          </el-dropdown-item>
          <el-dropdown-item @click="logout" :icon="Right"
            >로그아웃
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <img
      v-else
      class="loginbtn"
      @click="login"
      src="@/assets/kakao_login.png"
      alt="login"
      style="z-index: 3"
    />
  </div>

  <el-dialog
    v-model="state.mypageDialog"
    @close="mypageClose"
    width="40%"
    top="20px"
  >
    <div style="text-align: center">
      <img class="profile" :src="state.memberinfo.profileUrl" />
      <table class="mypageTable">
        <tbody>
          <tr>
            <td class="mypageLabel">MBTI</td>
            <td>{{ state.memberinfo.mbti }}</td>
          </tr>
          <tr>
            <td class="mypageLabel">닉네임</td>
            <td>{{ state.memberinfo.nickname }}</td>
          </tr>
          <tr>
            <td class="mypageLabel">성별</td>
            <td v-if="state.memberinfo.gender == 'MALE'">남자</td>
            <td v-else>여자</td>
          </tr>
          <tr>
            <td class="mypageLabel">생년월일</td>
            <td>{{ state.memberinfo.birth }}</td>
          </tr>
          <tr>
            <td class="mypageLabel">사는지역</td>
            <td>{{ state.memberinfo.sido }}</td>
          </tr>
          <tr>
            <td class="mypageLabel">관심사</td>
            <td>{{ state.interests }}</td>
          </tr>
        </tbody>
      </table>
      <el-button
        class="dropdown-css"
        style="margin-top: 20px"
        @click="mypageUpdateOpen"
        round
        size="large"
        >정보 수정</el-button
      >
    </div>
  </el-dialog>

  <el-dialog
    v-model="state.mypageUpdateDialog"
    @close="mypageUpdateClose"
    top="0px"
    width="40%"
  >
    <div class="mypage" style="text-align: center">
      <el-row class="filebox">
        <img class="profile" :src="state.member.profileUrl" />
        <label class="labelButton" for="file">프로필 사진 변경</label>
        <input type="file" id="file" @change="onFileSelected" />
      </el-row>
      <el-form
        style="margin-top: 10px; margin-bottom: 10px; align-items: center"
        ><table class="updateTable">
          <tbody>
            <tr>
              <td class="label">MBTI</td>
              <td class="data" align="left">
                <el-select style="width: 230px" v-model="state.member.mbti">
                  <el-option
                    v-for="item in option2"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </td>
            </tr>
            <tr>
              <td class="label">닉네임</td>
              <td class="data" align="left">
                <el-input
                  style="width: 230px"
                  v-model="state.member.nickname"
                />
                <button
                  @click.prevent="nameCheck"
                  style="
                    margin-left: 10px;
                    height: 32px;
                    padding: 9px;
                    vertical-align: bottom;
                  "
                  class="mainButton"
                >
                  중복확인
                </button>
              </td>
            </tr>
            <tr>
              <td class="label">성별</td>
              <td align="left">
                <el-radio-group v-model="state.member.gender" disabled>
                  <el-radio label="MALE">남자</el-radio>
                  <el-radio label="FEMALE">여자</el-radio>
                </el-radio-group>
              </td>
            </tr>
            <tr>
              <td class="label">생년월일</td>
              <td class="data" align="left">
                <el-date-picker
                  style="width: 230px"
                  v-model="state.member.birth"
                  type="date"
                  disabled
                />
              </td>
            </tr>

            <tr>
              <td class="label">지역</td>
              <td class="data" align="left">
                <el-select style="width: 230px" v-model="state.member.sido">
                  <el-option
                    v-for="item in option1"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </td>
            </tr>
            <tr>
              <td class="label">관심사</td>
              <td align="left">
                <el-checkbox-group
                  v-model="state.member.interests"
                  style="width: 350px; align-items: center"
                >
                  <el-checkbox label="캠핑" name="캠핑" />
                  <el-checkbox label="맛집탐방" name="맛집탐방" />
                  <el-checkbox label="코딩" name="코딩" />
                  <el-checkbox label="TV/영화" name="TV/영화" />
                  <el-checkbox label="스포츠" name="스포츠" />
                  <el-checkbox label="술" name="술" />
                  <el-checkbox label="음악" name="음악" />
                  <el-checkbox label="쇼핑" name="쇼핑" />
                  <el-checkbox label="자동차" name="자동차" />
                  <el-checkbox label="게임" name="게임" />
                  <el-checkbox label="동물" name="동물" />
                  <el-checkbox label="패션" name="패션" />
                  <el-checkbox label="뷰티" name="뷰티" />
                  <el-checkbox label="디자인" name="디자인" />
                </el-checkbox-group>
              </td>
            </tr>
          </tbody></table
      ></el-form>
      <el-footer style="height: 20px">
        <el-button
          @click.prevent="updateInfo"
          size="large"
          type="danger"
          plain
          round
          >수정</el-button
        >
        <el-button
          @click="state.confirmDialog = true"
          type="danger"
          size="large"
          round
          >탈퇴</el-button
        >
      </el-footer>
    </div>
  </el-dialog>

  <el-dialog top="250px" v-model="state.alertDialogVisible" width="30%" center>
    <el-row style="top: 12px; font-size: 16.5px">{{ state.alertMsg }}</el-row>
    <template #footer>
      <span class="dialog-footer">
        <el-button type="danger" round @click="state.alertDialogVisible = false"
          >확인</el-button
        >
      </span>
    </template>
  </el-dialog>
  <el-dialog v-model="state.confirmDialog" width="30%" center top="250px">
    <el-row style="top: 12px; font-size: 16.5px">{{ state.confirmMsg }}</el-row>
    <template #footer>
      <span class="dialog-footer">
        <el-button type="danger" round @click="deleteMember">탈퇴</el-button>
        <el-button
          type="danger"
          round
          plain
          @click="state.confirmDialog = false"
          >취소</el-button
        >
      </span>
    </template>
  </el-dialog>
</template>

<script>
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { Avatar, Comment, Right } from "@element-plus/icons-vue";
import { computed, onMounted, reactive } from "@vue/runtime-core";

export default {
  setup() {
    const router = useRouter();
    const store = useStore();
    onMounted(() => {
      store.dispatch("accounts/getMemberinfo").then(function (res) {
        store.commit("accounts/SET_MEMBER_INFO", res.data.body);
        state.memberinfo = computed(() => store.getters["accounts/getMember"]);
        changeMember();
      });
    });
    const state = reactive({
      memberinfo: computed(() => store.getters["accounts/getMember"]),
      mypageUpdateDialog: false,
      mypageDialog: false,
      interests: "",
      image: "",
      member: {
        mbti: "",
        profileUrl: "",
        nickname: "",
        gender: "",
        birth: "",
        sido: "",
        interests: [],
      },
      alertMsg: "",
      alertDialogVisible: false,
      confirmDialog: false,
      confirmMsg: "회원 탈퇴 하시겠습니까?",
    });
    const option1 = [
      {
        value: "서울",
        label: "서울",
      },
      {
        value: "인천",
        label: "인천",
      },
      {
        value: "경기",
        label: "경기",
      },
      {
        value: "강원",
        label: "강원",
      },
      {
        value: "부산",
        label: "부산",
      },
      {
        value: "대전",
        label: "대전",
      },
      {
        value: "울산",
        label: "울산",
      },
      {
        value: "충북",
        label: "충북",
      },
      {
        value: "충남",
        label: "충남",
      },
      {
        value: "세종",
        label: "세종",
      },
      {
        value: "대구",
        label: "대구",
      },
      {
        value: "경북",
        label: "경북",
      },
      {
        value: "경남",
        label: "경남",
      },
      {
        value: "광주",
        label: "광주",
      },
      {
        value: "전북",
        label: "전북",
      },
      {
        value: "전남",
        label: "전남",
      },
      {
        value: "제주",
        label: "제주",
      },
    ];
    const option2 = [
      {
        value: "INFJ",
        label: "INFJ",
      },
      {
        value: "INFP",
        label: "INFP",
      },
      {
        value: "INTJ",
        label: "INTJ",
      },
      {
        value: "INTP",
        label: "INTP",
      },
      {
        value: "ISFJ",
        label: "ISFJ",
      },
      {
        value: "ISFP",
        label: "ISFP",
      },
      {
        value: "ISTJ",
        label: "ISTJ",
      },
      {
        value: "ISTP",
        label: "ISTP",
      },
      {
        value: "ENFJ",
        label: "ENFJ",
      },
      {
        value: "ENFP",
        label: "ENFP",
      },
      {
        value: "ENTJ",
        label: "ENTJ",
      },
      {
        value: "ENTP",
        label: "ENTP",
      },
      {
        value: "ESFJ",
        label: "ESFJ",
      },
      {
        value: "ESFP",
        label: "ESFP",
      },
      {
        value: "ESTJ",
        label: "ESTJ",
      },
      {
        value: "ESTP",
        label: "ESTP",
      },
    ];

    const onFileSelected = function (event) {
      event.preventDefault();
      state.image = event.target.files[0];
      store
        .dispatch("accounts/profileUpload", state.image)
        .then(function (res) {
          console.log(res.data);
          store.commit("accounts/SET_MEMBER_INFO", res.data.body.member);
          state.member.profileUrl = state.memberinfo.profileUrl;
        });
    };

    const nameCheck = function () {
      const nickname = state.member.nickname;
      if (nickname === state.memberinfo.nickname) {
        alertDialog("현재와 같은 닉네임입니다.");
      } else {
        store
          .dispatch("accounts/getUserName", { nickname })
          .then(function (res) {
            if (res.data.body === true) {
              alertDialog("사용가능한 닉네임 입니다.");
            } else {
              alertDialog("중복 된 닉네임입니다.");
            }
          });
      }
    };
    const alertDialog = function (message) {
      state.alertMsg = message;
      state.alertDialogVisible = true;
    };
    const goHome = function () {
      router.push({ name: "HomeView" });
    };
    const goPeople = function () {
      router.push({ name: "friend" });
    };
    const goMyPage = function () {
      mypageOpen();
    };

    const mypageOpen = function () {
      if (state.memberinfo.interests.length != 0) {
        state.interests = "";
        for (
          let index = 0;
          index < state.memberinfo.interests.length;
          index++
        ) {
          state.interests += state.memberinfo.interests[index];
          if (index < state.memberinfo.interests.length - 1) {
            state.interests += ", ";
          }
        }
      } else {
        state.interests = "선택한 관심사가 없습니다.";
      }
      state.mypageDialog = true;
    };

    const mypageClose = function () {
      state.mypageDialog = false;
    };

    const mypageUpdateOpen = function () {
      state.mypageDialog = false;
      state.mypageUpdateDialog = true;
    };

    const mypageUpdateClose = function () {
      changeMember();
      state.mypageUpdateDialog = false;
    };
    const changeMember = function () {
      state.member.nickname = state.memberinfo.nickname;
      state.member.mbti = state.memberinfo.mbti;
      state.member.gender = state.memberinfo.gender;
      state.member.birth = state.memberinfo.birth;
      state.member.sido = state.memberinfo.sido;
      state.member.profileUrl = state.memberinfo.profileUrl;
      state.member.interests = state.memberinfo.interests;
    };
    const updateInfo = async function () {
      await store
        .dispatch("accounts/updateMemberinfo", {
          mbti: state.member.mbti,
          interests: state.member.interests,
          nickname: state.member.nickname,
          profileUrl: state.member.profileUrl,
          sido: state.member.sido,
        })
        .then(function (result) {
          store.commit("accounts/SET_MEMBER_INFO", result.data.body.member);
        })
        .catch(function (err) {
          console.log(err);
        });
      mypageUpdateClose();
    };

    const deleteMember = function () {
      // 회원 탈퇴
      store
        .dispatch("accounts/deleteMemberinfo")
        .then(function () {
          sessionStorage.removeItem("access-token");
          store.commit("accounts/SET_MEMBER_INFO", null);
          state.mypageUpdateDialog = false;
          router.push({ name: "HomeView" });
        })
        .catch(function (err) {
          console.log(err);
        });
      mypageUpdateClose();
      state.confirmDialog = false;
    };

    const login = () => {
      // 카카오톡 로그인 화면 전환
      window.location.replace(
        `https://kauth.kakao.com/oauth/authorize?client_id=${process.env.VUE_APP_KAKAO_CLIENT_ID}&redirect_uri=${process.env.VUE_APP_KAKAO_LOGIN_REDIRECT_URI}&response_type=code`
      );
    };

    const logout = () => {
      window.location.replace(
        `https://kauth.kakao.com/oauth/logout?client_id=${process.env.VUE_APP_KAKAO_CLIENT_ID}&logout_redirect_uri=${process.env.VUE_APP_KAKAO_LOGOUT_REDIRECT_URI}`
      );
    };

    return {
      state,
      option1,
      option2,
      onFileSelected,
      nameCheck,
      goHome,
      goPeople,
      goMyPage,
      mypageOpen,
      mypageClose,
      mypageUpdateOpen,
      mypageUpdateClose,
      updateInfo,
      deleteMember,
      login,
      logout,
      alertDialog,
      Avatar,
      Comment,
      Right,
    };
  },
};
</script>

<style scoped>
.loginbtn {
  margin: 20px;
  height: 40px;
  width: 100px;
  cursor: pointer;
}

.el-dropdown {
  display: flex;
  margin-top: 20px;
  margin-right: 30px;
  float: right;
}

.el-dropdown-link {
  border-radius: 50%;
  object-fit: cover;
  width: 50px;
  height: 50px;
}

.navbar {
  background-color: #fadce1;
  display: flex;
  justify-content: space-between;
}
.profile {
  border-radius: 50%;
  width: 200px;
  height: 200px;
}
.filebox label {
  display: inline-block;
  padding: 10px 20px;
  background-color: #ffffff;
  color: #606266;
  vertical-align: middle;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  margin-top: 10px;
}
.filebox label:hover {
  color: palevioletred;
  background-color: #fbeff1;
  border-color: #fbeff1;
}

.filebox input[type="file"] {
  position: absolute;
  width: 0;
  height: 0;
  overflow: hidden;
}
.mypageTable {
  margin-left: auto;
  margin-right: auto;
  width: 500px;
  font-size: 20px;
  border-spacing: 0 20px;
  border: 10px solid #fde2e2;
}
.mypageLabel {
  width: 150px;
  color: rgb(255, 91, 136);
}
/* .mypage {
  border: 5px solid #fadce1;
} */
.updateTable {
  margin-left: auto;
  margin-right: auto;
  border-spacing: 0 10px;
}
.updateTable tr {
  margin-top: 20px;
}
.label {
  width: 150px;
  color: rgb(255, 91, 136);
}
.data {
  width: 300px;
}

.mypage,
.dropdown-css,
.selected,
.el-dropdown__popper,
.el-dropdown-menu,
#el-id-3047-150 {
  --el-color-primary: #f56c6c;
  --el-color-primary-light-3: #f89898;
  --el-color-primary-light-5: #fab6b6;
  --el-color-primary-light-7: #fcd3d3;
  --el-color-primary-light-8: #fde2e2;
  --el-color-primary-light-9: #fef0f0;
  --el-color-primary-dark-2: #c45656;
  --el-select-input-focus-border-color: #f56c6c;
  --el-dropdown-menuItem-hover-color: #f56c6c;
  --el-dropdown-menuItem-hover-fill: #fef0f0;
  --el-select-input-focus-border-color: #f56c6c;
  --el-menu-hover-text-color: #f56c6c;
  --el-dialog-padding-primary: #f56c6c;
  --el-menu-active-color: #f56c6c;
  --el-menu-active-color: #f56c6c;
  --el-menu-hover-text-color: #f56c6c;
  --el-icon-active-color: #f56c6c;
}

.el-dialog__close,
#el-popper-container-9894 {
  --el-color-primary: #f56c6c;
  --el-color-primary-light-3: #f89898;
  --el-color-primary-light-5: #fab6b6;
  --el-color-primary-light-7: #fcd3d3;
  --el-color-primary-light-8: #fde2e2;
  --el-color-primary-light-9: #fef0f0;
  --el-color-primary-dark-2: #c45656;
  --el-select-input-focus-border-color: #f56c6c;
  --el-dropdown-menuItem-hover-color: #f56c6c;
  --el-dropdown-menuItem-hover-fill: #fef0f0;
  --el-select-input-focus-border-color: #f56c6c;
  --el-menu-hover-text-color: #f56c6c;
  --el-dialog-padding-primary: #f56c6c;
  --el-menu-active-color: #f56c6c;
  --el-menu-active-color: #f56c6c;
  --el-menu-hover-text-color: #f56c6c;
  --el-icon-active-color: #f56c6c;
}
</style>
