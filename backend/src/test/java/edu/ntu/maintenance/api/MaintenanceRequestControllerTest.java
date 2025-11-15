package edu.ntu.maintenance.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntu.maintenance.entity.AppUser;
import edu.ntu.maintenance.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MaintenanceRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository userRepository;

    @Test
    void studentCannotAssignTechnicians() throws Exception {
        String studentToken = login("alicelee");
        long requestId = createRequest(studentToken);

        Map<String, Object> assignPayload = new HashMap<>();
        assignPayload.put("technicianId", findUserId("tech_mario"));

        mockMvc.perform(patch("/api/requests/{id}/assign", requestId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(assignPayload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void managerAssignsTechnicianAndTechnicianMovesToInProgress() throws Exception {
        String studentToken = login("alicelee");
        long requestId = createRequest(studentToken);

        String managerToken = login("dorm_manager");
        Map<String, Object> assignPayload = new HashMap<>();
        assignPayload.put("technicianId", findUserId("tech_mario"));
        assignPayload.put("completionTarget", OffsetDateTime.now().plusDays(2));
        assignPayload.put("note", "Handle ASAP");

        mockMvc.perform(patch("/api/requests/{id}/assign", requestId)
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(assignPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ASSIGNED"))
                .andExpect(jsonPath("$.technician.username").value("tech_mario"));

        String technicianToken = login("tech_mario");
        Map<String, Object> statusPayload = Map.of(
                "status", "IN_PROGRESS",
                "note", "Looking into it"
        );

        mockMvc.perform(patch("/api/requests/{id}/status", requestId)
                        .header("Authorization", "Bearer " + technicianToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(statusPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    private String login(String username) throws Exception {
        Map<String, Object> payload = Map.of(
                "username", username,
                "password", "Password!23"
        );
        String content = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode node = objectMapper.readTree(content);
        String token = node.get("token").asText();
        assertThat(token).isNotBlank();
        return token;
    }

    private long createRequest(String token) throws Exception {
        Map<String, Object> payload = Map.of(
                "title", "Aircon leaking",
                "description", "Water dripping steadily",
                "dorm", "North Hill Block 10",
                "room", "10-101",
                "category", "Plumbing",
                "priority", "HIGH"
        );
        String content = mockMvc.perform(post("/api/requests")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(content).get("id").asLong();
    }

    private long findUserId(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow();
        return user.getId();
    }
}
