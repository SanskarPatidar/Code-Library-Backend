package com.sanskar.Code.Library.Backend.repository.branchpushrequest;

//@DataMongoTest
//@ActiveProfiles("dev")
//@Disabled
//class BranchPushRequestRepositoryTest {
//
//    @Autowired
//    BranchPushRequestRepository repository;
//
//    @BeforeEach
//    void setUp() {
//        repository.deleteAll();
//
//        BranchPushRequest req1 = BranchPushRequest.builder()
//                .id("1")
//                .requestedBy("user1")
//                .requestedAt(LocalDateTime.now().minusMinutes(10))
//                .snippetId("s1")
//                .build();
//
//        BranchPushRequest req2 = BranchPushRequest.builder()
//                .id("2")
//                .requestedBy("user1")
//                .requestedAt(LocalDateTime.now())
//                .snippetId("s2")
//                .build();
//
//        BranchPushRequest req3 = BranchPushRequest.builder()
//                .id("3")
//                .requestedBy("user1")
//                .requestedAt(LocalDateTime.now().plusMinutes(10))
//                .snippetId("s1")
//                .rejected(true)
//                .build();
//
//        repository.saveAll(List.of(req1, req2, req3));
//    }
//
//    @Test
//    void findByRequesterUsernameOrderByRequestedAtDescTest(){
//        var page = repository.findByRequesterUsernameOrderByRequestedAtDesc("user1", PageRequest.of(0, 10));
//        assertEquals(3, page.getTotalElements());
//        List<LocalDateTime> timeList = page.getContent()
//                .stream()
//                .map(BranchPushRequest::getRequestedAt)
//                .toList();
//        assertThat(timeList).isSortedAccordingTo(Comparator.reverseOrder());
//    }
//
//    @Test
//    void findBySnippetIdValidTest(){
//        var page = repository.findAllBySnippetIdAndValidAsPage("s1", PageRequest.of(0, 10));
//        assertEquals(1, page.getTotalElements());
//    }
//}