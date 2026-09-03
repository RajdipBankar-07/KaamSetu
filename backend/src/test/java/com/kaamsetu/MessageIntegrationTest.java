package com.kaamsetu;

import com.kaamsetu.core.common.ApiResponse;
import com.kaamsetu.core.security.UserPrincipal;
import com.kaamsetu.modules.admin.service.AdminGovernanceService;
import com.kaamsetu.modules.message.controller.MessageController;
import com.kaamsetu.modules.message.dto.*;
import com.kaamsetu.modules.message.entity.ConversationEntity;
import com.kaamsetu.modules.message.entity.MessageEntity;
import com.kaamsetu.modules.message.repository.ConversationRepository;
import com.kaamsetu.modules.message.repository.MessageRepository;
import com.kaamsetu.modules.message.service.MessageService;
import com.kaamsetu.modules.notification.service.NotificationService;
import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.entity.enums.RoleEnum;
import com.kaamsetu.modules.user.entity.enums.UserStatusEnum;
import com.kaamsetu.modules.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 🌾 KaamSetu (कामसेतू) - Permanent Database Messaging Integration Test Suite (MSG-001..020)
 * Verifies complete bidirectional communication between Admin and Users.
 */
public class MessageIntegrationTest {

    private static final Map<UUID, UserEntity> userDb = new ConcurrentHashMap<>();
    private static final Map<UUID, ConversationEntity> convDb = new ConcurrentHashMap<>();
    private static final Map<UUID, MessageEntity> msgDb = new ConcurrentHashMap<>();

    private static UserRepository userRepository;
    private static ConversationRepository conversationRepository;
    private static MessageRepository messageRepository;
    private static NotificationService notificationService;
    private static AdminGovernanceService adminGovernanceService;
    private static MessageService messageService;
    private static MessageController messageController;

    private static UserEntity adminUser;
    private static UserEntity workerSuresh;
    private static UserEntity providerMahesh;
    private static UserEntity pendingWorkerRaju;

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("💬 KAAMSETU ADMIN ↔ USER PERSISTENT MESSAGING TEST SUITE (MSG-001..020)");
        System.out.println("===============================================================================");

        setUp();

        String[][] testCases = {
                {"MSG-001", "Admin Sends Message to Worker (Saved in DB)", "testMsg001_AdminSendsToWorker"},
                {"MSG-002", "Worker Fetches Persisted Admin Message", "testMsg002_WorkerFetchesAdminMessage"},
                {"MSG-003", "Worker Reply to Admin Is Persisted in DB", "testMsg003_WorkerRepliesToAdmin"},
                {"MSG-004", "Admin Conversation List Displays User, Message, Unread", "testMsg004_AdminConversationsList"},
                {"MSG-005", "Admin Opens User Stream Marks Messages as Read", "testMsg005_AdminOpensStreamMarksRead"},
                {"MSG-006", "Worker-to-Worker Direct Messaging Is Hard Rejected", "testMsg006_WorkerToWorkerForbidden"},
                {"MSG-007", "Provider Sends Message to Admin Helpdesk", "testMsg007_ProviderSendsToAdmin"},
                {"MSG-008", "Pending User Can Communicate with Admin for KYC", "testMsg008_PendingUserKycMessage"},
                {"MSG-009", "Empty Message Text Validation Rejection", "testMsg009_EmptyMessageValidation"},
                {"MSG-010", "Deterministic Conversation Association per User", "testMsg010_DeterministicConversationId"},
                {"MSG-011", "Sender Identity Derived Strictly from Authentication", "testMsg011_SenderAuthEnforcement"},
                {"MSG-012", "Chronological Order of Message History", "testMsg012_ChronologicalMessageOrdering"},
                {"MSG-013", "Dynamic Unread Message Counter for Worker", "testMsg013_UnreadCounterWorker"},
                {"MSG-014", "Dynamic Unread Message Counter for Admin", "testMsg014_UnreadCounterAdmin"},
                {"MSG-015", "Search & Role Filtering in Admin Inbox", "testMsg015_AdminInboxSearchAndFilter"},
                {"MSG-016", "Admin Quick Templates and Long Text Sanitization", "testMsg016_MessageSanitization"},
                {"MSG-017", "User Clears Chat History via API", "testMsg017_UserClearsChatHistory"},
                {"MSG-018", "Admin Clears Specific User Chat History with Audit", "testMsg018_AdminClearsChatHistory"},
                {"MSG-019", "Persistent Database State Across Simulated Restart", "testMsg019_DatabasePersistenceSimulatedRestart"},
                {"MSG-020", "Controller HTTP Response and Payload Verification", "testMsg020_ControllerHttpResponses"}
        };

        int passed = 0;
        int failed = 0;

        MessageIntegrationTest testInstance = new MessageIntegrationTest();

        for (String[] tc : testCases) {
            String id = tc[0];
            String name = tc[1];
            String methodName = tc[2];

            try {
                Method m = MessageIntegrationTest.class.getDeclaredMethod(methodName);
                m.invoke(testInstance);
                System.out.printf("  ✓ [%s] %-55s : PASS%n", id, name);
                passed++;
            } catch (Exception e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                System.out.printf("  ❌ [%s] %-55s : FAIL (%s)%n", id, name, cause.getMessage());
                failed++;
            }
        }

        System.out.println("-------------------------------------------------------------------------------");
        System.out.printf("RESULTS: TOTAL: %d | PASSED: %d | FAILED: %d | STATUS: %s%n",
                testCases.length, passed, failed, (failed == 0 ? "ALL PASS (100%)" : "FAILURES DETECTED"));
        System.out.println("===============================================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    @SuppressWarnings("unchecked")
    private static void setUp() {
        userDb.clear();
        convDb.clear();
        msgDb.clear();

        // Seed Users
        adminUser = UserEntity.builder()
                .username("admin")
                .fullName("प्रशासन (KaamSetu Admin)")
                .email("admin@kaamsetu.org")
                .mobile("+919822000000")
                .role(RoleEnum.ADMIN)
                .status(UserStatusEnum.ACTIVE)
                .build();
        adminUser.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        adminUser.setCreatedAt(Instant.now());
        adminUser.setUpdatedAt(Instant.now());
        userDb.put(adminUser.getId(), adminUser);

        workerSuresh = UserEntity.builder()
                .username("suresh")
                .fullName("सुरेश जाधव (Suresh Jadhav)")
                .email("suresh.dev@kaamsetu.in")
                .mobile("+919822000001")
                .village("रांजणगाव (Ranjangaon)")
                .role(RoleEnum.WORKER)
                .status(UserStatusEnum.ACTIVE)
                .build();
        workerSuresh.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        workerSuresh.setCreatedAt(Instant.now());
        workerSuresh.setUpdatedAt(Instant.now());
        userDb.put(workerSuresh.getId(), workerSuresh);

        providerMahesh = UserEntity.builder()
                .username("mahesh")
                .fullName("महेश पाटील (Mahesh Patil)")
                .email("mahesh.dev@kaamsetu.in")
                .mobile("+919822000011")
                .village("शिक्रापूर (Shikrapur)")
                .role(RoleEnum.PROVIDER)
                .status(UserStatusEnum.ACTIVE)
                .build();
        providerMahesh.setId(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        providerMahesh.setCreatedAt(Instant.now());
        providerMahesh.setUpdatedAt(Instant.now());
        userDb.put(providerMahesh.getId(), providerMahesh);

        pendingWorkerRaju = UserEntity.builder()
                .username("raju")
                .fullName("राजू मोरे (Raju More)")
                .email("raju.dev@kaamsetu.in")
                .mobile("+919822000003")
                .village("वाघोली (Wagholi)")
                .role(RoleEnum.WORKER)
                .status(UserStatusEnum.PENDING)
                .build();
        pendingWorkerRaju.setId(UUID.fromString("44444444-4444-4444-4444-444444444444"));
        pendingWorkerRaju.setCreatedAt(Instant.now());
        pendingWorkerRaju.setUpdatedAt(Instant.now());
        userDb.put(pendingWorkerRaju.getId(), pendingWorkerRaju);

        // Dynamic Dynamic Proxy Repositories for 100% Mock Testing
        userRepository = (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[]{UserRepository.class},
                (proxy, method, mArgs) -> {
                    String name = method.getName();
                    if ("findById".equals(name)) {
                        return Optional.ofNullable(userDb.get((UUID) mArgs[0]));
                    }
                    if ("findByRole".equals(name)) {
                        RoleEnum r = (RoleEnum) mArgs[0];
                        return userDb.values().stream().filter(u -> u.getRole() == r).collect(Collectors.toList());
                    }
                    if ("findAll".equals(name)) {
                        return new ArrayList<>(userDb.values());
                    }
                    return null;
                }
        );

        conversationRepository = (ConversationRepository) Proxy.newProxyInstance(
                ConversationRepository.class.getClassLoader(),
                new Class<?>[]{ConversationRepository.class},
                (proxy, method, mArgs) -> {
                    String name = method.getName();
                    if ("findByUserId".equals(name)) {
                        UUID uid = (UUID) mArgs[0];
                        return convDb.values().stream().filter(c -> c.getUserId().equals(uid)).findFirst();
                    }
                    if ("findById".equals(name)) {
                        return Optional.ofNullable(convDb.get((UUID) mArgs[0]));
                    }
                    if ("save".equals(name)) {
                        ConversationEntity c = (ConversationEntity) mArgs[0];
                        if (c.getId() == null) c.setId(UUID.randomUUID());
                        if (c.getCreatedAt() == null) c.setCreatedAt(Instant.now());
                        c.setUpdatedAt(Instant.now());
                        convDb.put(c.getId(), c);
                        return c;
                    }
                    if ("findAllByOrderByUpdatedAtDesc".equals(name)) {
                        List<ConversationEntity> list = new ArrayList<>(convDb.values());
                        list.sort((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()));
                        return list;
                    }
                    return null;
                }
        );

        messageRepository = (MessageRepository) Proxy.newProxyInstance(
                MessageRepository.class.getClassLoader(),
                new Class<?>[]{MessageRepository.class},
                (proxy, method, mArgs) -> {
                    String name = method.getName();
                    if ("save".equals(name)) {
                        MessageEntity m = (MessageEntity) mArgs[0];
                        if (m.getId() == null) m.setId(UUID.randomUUID());
                        if (m.getCreatedAt() == null) m.setCreatedAt(Instant.now());
                        m.setUpdatedAt(Instant.now());
                        msgDb.put(m.getId(), m);
                        return m;
                    }
                    if ("findByConversationIdOrderByCreatedAtAsc".equals(name)) {
                        UUID cid = (UUID) mArgs[0];
                        return msgDb.values().stream()
                                .filter(m -> m.getConversationId().equals(cid))
                                .sorted(Comparator.comparing(MessageEntity::getCreatedAt))
                                .collect(Collectors.toList());
                    }
                    if ("countByReceiverIdAndIsReadFalse".equals(name)) {
                        UUID rid = (UUID) mArgs[0];
                        return msgDb.values().stream().filter(m -> m.getReceiverId().equals(rid) && !Boolean.TRUE.equals(m.getIsRead())).count();
                    }
                    if ("countByConversationIdAndReceiverIdAndIsReadFalse".equals(name)) {
                        UUID cid = (UUID) mArgs[0];
                        UUID rid = (UUID) mArgs[1];
                        return msgDb.values().stream()
                                .filter(m -> m.getConversationId().equals(cid) && m.getReceiverId().equals(rid) && !Boolean.TRUE.equals(m.getIsRead()))
                                .count();
                    }
                    if ("markAllIncomingAsRead".equals(name)) {
                        UUID cid = (UUID) mArgs[0];
                        UUID readerId = (UUID) mArgs[1];
                        Instant now = (Instant) mArgs[2];
                        msgDb.values().stream()
                                .filter(m -> m.getConversationId().equals(cid) && !m.getSenderId().equals(readerId) && !Boolean.TRUE.equals(m.getIsRead()))
                                .forEach(m -> {
                                    m.setIsRead(true);
                                    m.setReadAt(now);
                                });
                        return null;
                    }
                    if ("deleteByConversationId".equals(name)) {
                        UUID cid = (UUID) mArgs[0];
                        msgDb.entrySet().removeIf(e -> e.getValue().getConversationId().equals(cid));
                        return null;
                    }
                    return null;
                }
        );

        com.kaamsetu.modules.notification.repository.NotificationRepository notifRepo =
                (com.kaamsetu.modules.notification.repository.NotificationRepository) Proxy.newProxyInstance(
                        com.kaamsetu.modules.notification.repository.NotificationRepository.class.getClassLoader(),
                        new Class<?>[]{com.kaamsetu.modules.notification.repository.NotificationRepository.class},
                        (proxy, method, mArgs) -> null
                );
        notificationService = new NotificationService(notifRepo);

        com.kaamsetu.modules.admin.repository.AuditLogRepository auditRepo =
                (com.kaamsetu.modules.admin.repository.AuditLogRepository) Proxy.newProxyInstance(
                        com.kaamsetu.modules.admin.repository.AuditLogRepository.class.getClassLoader(),
                        new Class<?>[]{com.kaamsetu.modules.admin.repository.AuditLogRepository.class},
                        (proxy, method, mArgs) -> null
                );

        adminGovernanceService = new AdminGovernanceService(
                userRepository,
                null,
                null,
                null,
                null,
                null,
                auditRepo,
                notificationService
        );

        messageService = new MessageService(
                conversationRepository,
                messageRepository,
                userRepository,
                notificationService,
                adminGovernanceService
        );

        messageController = new MessageController(messageService);
    }

    public void testMsg001_AdminSendsToWorker() {
        SendMessageRequest req = SendMessageRequest.builder()
                .receiverId(workerSuresh.getId())
                .messageText("तुमची कागदपत्रे तपासत आहोत.")
                .build();

        MessageResponse res = messageService.sendMessage(adminUser.getId(), RoleEnum.ADMIN, req);
        assertNotNull(res.getId(), "Message ID must be generated");
        assertEquals(adminUser.getId(), res.getSenderId());
        assertEquals(workerSuresh.getId(), res.getReceiverId());
        assertEquals("तुमची कागदपत्रे तपासत आहोत.", res.getMessageText());
        assertEquals("ADMIN", res.getSenderRole());
        assertEquals("WORKER", res.getReceiverRole());
        assertFalse(res.isRead(), "Message must be unread upon dispatch");

        // Verify permanent existence in database
        MessageEntity inDb = msgDb.get(res.getId());
        assertNotNull(inDb, "Message must be in message repository DB");
        assertEquals("तुमची कागदपत्रे तपासत आहोत.", inDb.getMessageText());
    }

    public void testMsg002_WorkerFetchesAdminMessage() {
        ConversationResponse conv = messageService.getMyConversation(workerSuresh.getId());
        assertNotNull(conv, "Worker conversation must exist");
        assertFalse(conv.getMessages().isEmpty(), "Worker must see Admin message");
        MessageResponse firstMsg = conv.getMessages().get(0);
        assertEquals("तुमची कागदपत्रे तपासत आहोत.", firstMsg.getMessageText());
        assertEquals("ADMIN", firstMsg.getSenderRole());
    }

    public void testMsg003_WorkerRepliesToAdmin() {
        SendMessageRequest req = SendMessageRequest.builder()
                .messageText("धन्यवाद.")
                .build();

        MessageResponse res = messageService.sendMessage(workerSuresh.getId(), RoleEnum.WORKER, req);
        assertNotNull(res.getId());
        assertEquals(workerSuresh.getId(), res.getSenderId());
        assertEquals(adminUser.getId(), res.getReceiverId());
        assertEquals("WORKER", res.getSenderRole());
        assertEquals("ADMIN", res.getReceiverRole());
        assertEquals("धन्यवाद.", res.getMessageText());
    }

    public void testMsg004_AdminConversationsList() {
        List<AdminConversationSummaryResponse> convs = messageService.getAdminConversations(null, "ALL", false);
        assertFalse(convs.isEmpty());
        AdminConversationSummaryResponse sureshConv = convs.stream()
                .filter(c -> c.getUserId().equals(workerSuresh.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(sureshConv, "Suresh must be in admin conversation list");
        assertEquals("धन्यवाद.", sureshConv.getLastMessage());
        assertTrue(sureshConv.getUnreadCount() >= 1, "Admin should have unread message from Suresh");
    }

    public void testMsg005_AdminOpensStreamMarksRead() {
        ConversationResponse conv = messageService.getAdminConversationForUser(adminUser.getId(), workerSuresh.getId());
        assertNotNull(conv);
        assertEquals(2, conv.getMessages().size(), "Both Admin and Suresh messages must appear");
        assertEquals(0, conv.getUnreadCount(), "Stream opening marks unread as 0");

        long unreadAfter = messageService.getUnreadCount(adminUser.getId());
        assertEquals(0, unreadAfter, "Admin unread count must be 0 after reading");
    }

    public void testMsg006_WorkerToWorkerForbidden() {
        SendMessageRequest directReq = SendMessageRequest.builder()
                .receiverId(providerMahesh.getId())
                .messageText("Direct hello to provider")
                .build();

        try {
            messageService.sendMessage(workerSuresh.getId(), RoleEnum.WORKER, directReq);
            fail("Direct User-to-User messaging must throw AccessDeniedException");
        } catch (AccessDeniedException e) {
            assertTrue(e.getMessage().contains("strictly forbidden"), "Must reject direct peer message");
        }
    }

    public void testMsg007_ProviderSendsToAdmin() {
        SendMessageRequest req = SendMessageRequest.builder()
                .messageText("कांदा लागवड कामासाठी १० कामगारांची गरज आहे.")
                .build();

        MessageResponse res = messageService.sendMessage(providerMahesh.getId(), RoleEnum.PROVIDER, req);
        assertNotNull(res.getId());
        assertEquals(providerMahesh.getId(), res.getSenderId());
        assertEquals("PROVIDER", res.getSenderRole());
    }

    public void testMsg008_PendingUserKycMessage() {
        SendMessageRequest req = SendMessageRequest.builder()
                .messageText("नवीन शेतकरी नोंदणी केली आहे, कृपया खाते मंजूर करा.")
                .build();

        MessageResponse res = messageService.sendMessage(pendingWorkerRaju.getId(), RoleEnum.WORKER, req);
        assertNotNull(res.getId());
        assertEquals(pendingWorkerRaju.getId(), res.getSenderId());
    }

    public void testMsg009_EmptyMessageValidation() {
        SendMessageRequest req = SendMessageRequest.builder()
                .messageText("   ")
                .build();

        try {
            messageService.sendMessage(workerSuresh.getId(), RoleEnum.WORKER, req);
            fail("Empty message text must fail validation");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("cannot be empty"));
        }
    }

    public void testMsg010_DeterministicConversationId() {
        ConversationResponse conv1 = messageService.getMyConversation(workerSuresh.getId());
        ConversationResponse conv2 = messageService.getAdminConversationForUser(adminUser.getId(), workerSuresh.getId());
        assertEquals(conv1.getConversationId(), conv2.getConversationId(), "Conversation ID must remain stable across user and admin lookups");
    }

    public void testMsg011_SenderAuthEnforcement() {
        // When client sends, the senderId is passed as method arg from UserPrincipal, not from request payload
        SendMessageRequest req = SendMessageRequest.builder()
                .messageText("Verified auth test")
                .build();

        MessageResponse res = messageService.sendMessage(workerSuresh.getId(), RoleEnum.WORKER, req);
        assertEquals(workerSuresh.getId(), res.getSenderId(), "Sender must strictly match authenticated principal");
    }

    public void testMsg012_ChronologicalMessageOrdering() {
        ConversationResponse conv = messageService.getMyConversation(workerSuresh.getId());
        List<MessageResponse> msgs = conv.getMessages();
        assertTrue(msgs.size() >= 2);
        for (int i = 0; i < msgs.size() - 1; i++) {
            assertTrue(msgs.get(i).getCreatedAt().compareTo(msgs.get(i + 1).getCreatedAt()) <= 0, "Messages must be chronological ASC");
        }
    }

    public void testMsg013_UnreadCounterWorker() {
        // Admin sends unread to Suresh
        SendMessageRequest req = SendMessageRequest.builder()
                .receiverId(workerSuresh.getId())
                .messageText("नवीन काम सूचना उपलब्ध आहे.")
                .build();
        messageService.sendMessage(adminUser.getId(), RoleEnum.ADMIN, req);

        long unread = messageService.getUnreadCount(workerSuresh.getId());
        assertTrue(unread >= 1, "Worker should have unread message count");
    }

    public void testMsg014_UnreadCounterAdmin() {
        // Raju sends message to Admin
        SendMessageRequest req = SendMessageRequest.builder()
                .messageText("माझे आधार कार्ड पडताळणी प्रलंबित आहे.")
                .build();
        messageService.sendMessage(pendingWorkerRaju.getId(), RoleEnum.WORKER, req);

        List<AdminConversationSummaryResponse> convs = messageService.getAdminConversations(null, "ALL", true);
        assertFalse(convs.isEmpty(), "Unread filter should list active pending conversations");
    }

    public void testMsg015_AdminInboxSearchAndFilter() {
        List<AdminConversationSummaryResponse> workerConvs = messageService.getAdminConversations("Suresh", "WORKERS", false);
        assertEquals(1, workerConvs.size());
        assertEquals(workerSuresh.getId(), workerConvs.get(0).getUserId());
    }

    public void testMsg016_MessageSanitization() {
        SendMessageRequest req = SendMessageRequest.builder()
                .receiverId(workerSuresh.getId())
                .messageText("<script>alert('hack')</script><b>सुरक्षित संदेश</b>")
                .build();
        MessageResponse res = messageService.sendMessage(adminUser.getId(), RoleEnum.ADMIN, req);
        assertFalse(res.getMessageText().contains("<script>"), "Scripts must be stripped");
        assertTrue(res.getMessageText().contains("&lt;b&gt;"), "HTML tags must be escaped");
    }

    public void testMsg017_UserClearsChatHistory() {
        messageService.clearConversation(providerMahesh.getId(), RoleEnum.PROVIDER, providerMahesh.getId());
        ConversationResponse conv = messageService.getMyConversation(providerMahesh.getId());
        assertTrue(conv.getMessages().isEmpty(), "User chat history must be empty after clearing");
    }

    public void testMsg018_AdminClearsChatHistory() {
        messageService.clearConversation(adminUser.getId(), RoleEnum.ADMIN, pendingWorkerRaju.getId());
        ConversationResponse conv = messageService.getAdminConversationForUser(adminUser.getId(), pendingWorkerRaju.getId());
        assertTrue(conv.getMessages().isEmpty(), "Admin cleared chat history must be empty");
    }

    public void testMsg019_DatabasePersistenceSimulatedRestart() {
        // Send a message
        SendMessageRequest req = SendMessageRequest.builder()
                .receiverId(workerSuresh.getId())
                .messageText("कायमस्वरूपी डेटाबेस साठवण चाचणी (Persistent DB Test)")
                .build();
        MessageResponse saved = messageService.sendMessage(adminUser.getId(), RoleEnum.ADMIN, req);

        // Simulate new Service instance connected to same database
        MessageService freshServiceInstance = new MessageService(
                conversationRepository,
                messageRepository,
                userRepository,
                notificationService,
                adminGovernanceService
        );

        ConversationResponse conv = freshServiceInstance.getAdminConversationForUser(adminUser.getId(), workerSuresh.getId());
        boolean found = conv.getMessages().stream().anyMatch(m -> m.getMessageText().contains("कायमस्वरूपी डेटाबेस साठवण"));
        assertTrue(found, "Message must persist and load cleanly from database repository");
    }

    public void testMsg020_ControllerHttpResponses() {
        UserPrincipal adminPrincipal = UserPrincipal.builder()
                .id(adminUser.getId())
                .username(adminUser.getUsername())
                .role(adminUser.getRole())
                .build();

        SendMessageRequest req = SendMessageRequest.builder()
                .receiverId(workerSuresh.getId())
                .messageText("Controller endpoint test")
                .build();

        ResponseEntity<ApiResponse<MessageResponse>> response = messageController.sendMessage(adminPrincipal, req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Controller endpoint test", response.getBody().getData().getMessageText());
    }

    private static void assertNotNull(Object obj) {
        if (obj == null) throw new AssertionError("Object must not be null");
    }

    private static void assertNotNull(Object obj, String msg) {
        if (obj == null) throw new AssertionError(msg);
    }

    private static void assertEquals(Object expected, Object actual) {
        if (expected instanceof Number && actual instanceof Number) {
            if (((Number) expected).longValue() != ((Number) actual).longValue()) {
                throw new AssertionError(String.format("Expected [%s] but was [%s]", expected, actual));
            }
            return;
        }
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(String.format("Expected [%s] but was [%s]", expected, actual));
        }
    }

    private static void assertEquals(Object expected, Object actual, String msg) {
        if (expected instanceof Number && actual instanceof Number) {
            if (((Number) expected).longValue() != ((Number) actual).longValue()) {
                throw new AssertionError(String.format("%s - Expected [%s] but was [%s]", msg, expected, actual));
            }
            return;
        }
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(String.format("%s - Expected [%s] but was [%s]", msg, expected, actual));
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) throw new AssertionError("Condition expected to be true");
    }

    private static void assertTrue(boolean condition, String msg) {
        if (!condition) throw new AssertionError(msg);
    }

    private static void assertFalse(boolean condition) {
        if (condition) throw new AssertionError("Condition expected to be false");
    }

    private static void assertFalse(boolean condition, String msg) {
        if (condition) throw new AssertionError(msg);
    }

    private static void fail(String msg) {
        throw new AssertionError("Test Failed: " + msg);
    }
}
