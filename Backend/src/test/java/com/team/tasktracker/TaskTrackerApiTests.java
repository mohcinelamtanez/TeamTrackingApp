package com.team.tasktracker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;
import com.team.tasktracker.common.WeekUtils;
import com.team.tasktracker.user.Role;
import com.team.tasktracker.user.User;
import com.team.tasktracker.user.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskTrackerApiTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    LocalDate monday = WeekUtils.weekStartOf(LocalDate.now());
    String supportToken;
    User agentA;
    User agentB;
    User agentC;

    @BeforeEach
    void setUp() throws Exception {
        agentA = createAgent("agent.a", "Agent A");
        agentB = createAgent("agent.b", "Agent B");
        agentC = createAgent("agent.c", "Agent C");
        supportToken = login("support", "support123");
    }

    @Test
    void rejectsBadCredentialsAndMissingToken() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"support\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/me/assignments")).andExpect(status().isUnauthorized());
    }

    @Test
    void agentCannotUseSupportEndpointsAndSupportCannotUseAgentEndpoints() throws Exception {
        String agentToken = login("agent.a", "password");
        mvc.perform(auth(get("/api/assignments").param("weekStart", monday.toString()), agentToken))
                .andExpect(status().isForbidden());
        mvc.perform(auth(get("/api/reports/daily"), agentToken)).andExpect(status().isForbidden());
        mvc.perform(auth(get("/api/me/assignments"), supportToken)).andExpect(status().isForbidden());
    }

    @Test
    void supportCanAssignAnyNumberOfAgentsToATaskType() throws Exception {
        assign(agentA, "VOIP");
        assign(agentB, "VOIP");
        assign(agentC, "VOIP");
        assign(agentC, "GTPS");

        mvc.perform(auth(get("/api/assignments").param("weekStart", monday.toString()), supportToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));

        mvc.perform(auth(post("/api/assignments"), supportToken).contentType(MediaType.APPLICATION_JSON)
                        .content(assignmentJson(agentA, "VOIP", monday)))
                .andExpect(status().isConflict());
        mvc.perform(auth(post("/api/assignments"), supportToken).contentType(MediaType.APPLICATION_JSON)
                        .content(assignmentJson(agentA, "GTPS", monday.plusDays(1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agentCompletesOnlyTheirOwnAssignment() throws Exception {
        long assignmentOfA = assign(agentA, "WIP_IP");
        String tokenA = login("agent.a", "password");
        String tokenB = login("agent.b", "password");

        mvc.perform(auth(put("/api/me/assignments/{id}/completions/today", assignmentOfA), tokenB))
                .andExpect(status().isNotFound());

        mvc.perform(auth(put("/api/me/assignments/{id}/completions/today", assignmentOfA), tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()));

        mvc.perform(auth(get("/api/me/assignments"), tokenA))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].completions.length()").value(1));
        mvc.perform(auth(get("/api/me/assignments"), tokenB))
                .andExpect(jsonPath("$.length()").value(0));

        // History is kept: an assignment with completions cannot be removed.
        mvc.perform(auth(delete("/api/assignments/{id}", assignmentOfA), supportToken))
                .andExpect(status().isConflict());
    }

    @Test
    void helpIsRecordedSeparatelyFromTheOfficialAssignment() throws Exception {
        long assignmentOfA = assign(agentA, "WIP_IP");
        String tokenA = login("agent.a", "password");

        mvc.perform(auth(post("/api/me/help"), tokenA).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskType\":\"VOIP\",\"note\":\"Queue was long\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskType").value("VOIP"))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.createdAt").exists());

        // Helping with your own official task is not "additional" help.
        mvc.perform(auth(post("/api/me/help"), tokenA).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskType\":\"WIP_IP\"}"))
                .andExpect(status().isBadRequest());

        // The official assignment is unchanged.
        mvc.perform(auth(get("/api/me/assignments"), tokenA))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(assignmentOfA))
                .andExpect(jsonPath("$[0].taskType").value("WIP_IP"));

        mvc.perform(auth(get("/api/reports/daily"), supportToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignments.length()").value(1))
                .andExpect(jsonPath("$.assignments[0].taskType").value("WIP_IP"))
                .andExpect(jsonPath("$.help.length()").value(1))
                .andExpect(jsonPath("$.help[0].agentName").value("Agent A"))
                .andExpect(jsonPath("$.help[0].taskType").value("VOIP"));
    }

    @Test
    void deactivatedUserTokenStopsWorking() throws Exception {
        String tokenA = login("agent.a", "password");
        mvc.perform(auth(patch("/api/users/{id}", agentA.getId()), supportToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"active\":false}"))
                .andExpect(status().isOk());
        mvc.perform(auth(get("/api/me/assignments"), tokenA)).andExpect(status().isUnauthorized());
    }

    private User createAgent(String username, String fullName) {
        return userRepository.save(new User(username, passwordEncoder.encode("password"), fullName, Role.AGENT));
    }

    private String login(String username, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    private long assign(User agent, String taskType) throws Exception {
        String body = mvc.perform(auth(post("/api/assignments"), supportToken).contentType(MediaType.APPLICATION_JSON)
                        .content(assignmentJson(agent, taskType, monday)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private static String assignmentJson(User agent, String taskType, LocalDate weekStart) {
        return "{\"agentId\":" + agent.getId() + ",\"taskType\":\"" + taskType + "\",\"weekStart\":\"" + weekStart
                + "\"}";
    }

    private static MockHttpServletRequestBuilder auth(MockHttpServletRequestBuilder request, String token) {
        return request.header("Authorization", "Bearer " + token);
    }
}
