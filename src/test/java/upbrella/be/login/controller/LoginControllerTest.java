package upbrella.be.login.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import upbrella.be.docs.utils.RestDocsSupport;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse;

public class LoginControllerTest extends RestDocsSupport {
    @Override
    protected Object initController() {
        return new LoginController();
    }

    @Test
    @DisplayName("사용자는 카카오 소셜 로그인을 할 수 있다.")
    void kakoLoginTest() throws Exception {
        // given
        String code = "{\"code\":\"1kdfjq0243f\"}";


        // when
        mockMvc.perform(
                        post("/oauth/kakao")
                                .content(code)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("kakao-login-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("code")
                                        .description("카카오 로그인 인증 코드")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("사용자 식별자"),
                                fieldWithPath("data.socialId").type(JsonFieldType.NUMBER)
                                        .description("사용자 소셜 식별자"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("사용자 이름"),
                                fieldWithPath("data.phoneNumber").type(JsonFieldType.STRING)
                                        .description("사용자 전화번호"),
                                fieldWithPath("data.adminStatus").type(JsonFieldType.BOOLEAN)
                                        .description("관리자 여부")
                        )));
    }

    @Test
    @DisplayName("사용자는 네이버 소셜 로그인을 할 수 있다.")
    void naverLoginTest() throws Exception {
        // given
        String code = "{\"code\":\"1kdfjq0243f\"}";


        // when
        mockMvc.perform(
                        post("/oauth/naver")
                                .content(code)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("naver-login-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("code").description("네이버 로그인 인증 코드")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("사용자 식별자"),
                                fieldWithPath("data.socialId").type(JsonFieldType.NUMBER)
                                        .description("사용자 소셜 식별자"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("사용자 이름"),
                                fieldWithPath("data.phoneNumber").type(JsonFieldType.STRING)
                                        .description("사용자 전화번호"),
                                fieldWithPath("data.adminStatus").type(JsonFieldType.BOOLEAN)
                                        .description("관리자 여부")
                        )));
    }
}
