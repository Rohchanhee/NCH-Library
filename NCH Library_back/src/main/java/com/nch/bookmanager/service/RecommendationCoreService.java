//package com.nch.bookmanager.service;
//
//import com.nch.bookmanager.dto.UserPreferenceDto;
//import com.nch.bookmanager.entity.Book;
//import com.nch.bookmanager.entity.User;
//import com.nch.bookmanager.repository.BookRepository;
//import com.nch.bookmanager.repository.RentalRecordRepository;
//import com.nch.bookmanager.repository.UserRepository;
//import com.nch.bookmanager.utils.SimilarityUtil;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional(readOnly = true)
//public class RecommendationCoreService {
//
//    private final UserRepository userRepository;
//    private final RentalService rentalService;
//    private final RentalRecordRepository rentalRecordRepository;
//    private final BookRepository bookRepository;
//
//    private static final int NEIGHBOR_COUNT = 5; // 이웃 사용자 수
//    private static final int RECOMMENDATION_LIMIT = 5; // 최종 추천 도서 수
//
//    public RecommendationCoreService(UserRepository userRepository,
//                                     RentalService rentalService,
//                                     RentalRecordRepository rentalRecordRepository,
//                                     BookRepository bookRepository) {
//        this.userRepository = userRepository;
//        this.rentalService = rentalService;
//        this.rentalRecordRepository = rentalRecordRepository;
//        this.bookRepository = bookRepository;
//    }
//
//    /**
//     * 코사인 유사도를 이용한 이웃 사용자 기반 추천 로직
//     */
//    public List<Book> recommendByUserBased(String currentUsername) {
//
//        // 1. 현재 사용자 및 모든 사용자의 선호 정보 (빈도수 Map) 조회
//        UserPreferenceDto currentUserPrefs = rentalService.getUserPreferences(currentUsername);
//        List<User> allUsers = userRepository.findAll();
//
//        // 2. 다른 사용자들과의 유사도 계산 및 유사도 랭킹 맵 생성
//        // Map<Username, SimilarityScore>
//        Map<String, Double> similarityScores = new HashMap<>();
//
//        for (User otherUser : allUsers) {
//            String otherUsername = otherUser.getUsername();
//            // 자기 자신은 비교에서 제외
//            if (otherUsername.equals(currentUsername)) {
//                continue;
//            }
//
//            UserPreferenceDto otherUserPrefs = rentalService.getUserPreferences(otherUsername);
//
//            // 3. 코사인 유사도 계산 (저자, 출판사, KDC 특징을 통합하여 비교)
//            double simScore = calculateCombinedCosineSimilarity(currentUserPrefs, otherUserPrefs);
//            similarityScores.put(otherUsername, simScore);
//        }
//
//        // 4. 유사도 기준 상위 N명(5명)의 이웃 선정
//        List<String> topNeighbors = similarityScores.entrySet().stream()
//                .sorted(Map.Entry.<String, Double>comparingByValue().reversed()) // 유사도 내림차순 정렬
//                .limit(NEIGHBOR_COUNT)
//                .map(Map.Entry::getKey)
//                .toList();
//
//        // 5. 이웃이 읽었지만, 현재 사용자가 읽지 않은 도서 추출 (자카드 유사도 논리 활용)
//        if (topNeighbors.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        // 현재 사용자가 대출했던 모든 도서 ID 집합 (내가 읽은 책)
//        Set<Long> myRentedBookIds = rentalRecordRepository.findRentalRecordsByUsername(currentUsername).stream()
//                .map(record -> record.getBook().getId())
//                .collect(Collectors.toSet());
//
//        // 이웃들이 대출한 도서 ID 집합 (후보)
//        Set<Long> neighborRentedBookIds = new HashSet<>();
//        for (String neighborUsername : topNeighbors) {
//            rentalRecordRepository.findRentalRecordsByUsername(neighborUsername).stream()
//                    .map(record -> record.getBook().getId())
//                    .forEach(neighborRentedBookIds::add);
//        }
//
//        // 6. 추천 도서 ID 최종 선정 (이웃이 읽었지만 내가 읽지 않은 책 = neighborRentedBookIds - myRentedBookIds)
//        // Set의 차집합 연산: 이웃 책 목록에서 내 책 목록을 제거
//        neighborRentedBookIds.removeAll(myRentedBookIds);
//
//        List<Long> recommendedBookIds = neighborRentedBookIds.stream().toList();
//
//        // 7. 최종 추천 도서 리스트 반환 (최대 RECOMMENDATION_LIMIT)
//        // (주의: 추천 후보가 많을 경우, 여기서 랭킹 로직(예: 이웃들 사이의 인기순)이 필요하지만,
//        // 현재는 단순하게 상위 N개만 가져오겠습니다.)
//        List<Long> finalIds = recommendedBookIds.stream()
//                .limit(RECOMMENDATION_LIMIT)
//                .toList();
//
//        return bookRepository.findAllById(finalIds);
//    }
//
//    /**
//     * 저자, 출판사, KDC 빈도수 Map을 결합하여 코사인 유사도를 계산
//     * @return 통합 코사인 유사도 (각 특징별 유사도의 단순 평균)
//     */
//    private double calculateCombinedCosineSimilarity(UserPreferenceDto userA, UserPreferenceDto userB) {
//
//        // 1. 저자 유사도
//        double authorSim = SimilarityUtil.calculateCosineSimilarity(userA.getAuthorCounts(), userB.getAuthorCounts());
//
//        // 2. 출판사 유사도
//        double publisherSim = SimilarityUtil.calculateCosineSimilarity(userA.getPublisherCounts(), userB.getPublisherCounts());
//
//        // 3. KDC 유사도
//        double kdcSim = SimilarityUtil.calculateCosineSimilarity(userA.getKdcCounts(), userB.getKdcCounts());
//
//        // 4. 세 유사도의 평균을 최종 통합 유사도로 사용
//        return (authorSim + publisherSim + kdcSim) / 3.0;
//    }
//}

package com.nch.bookmanager.service;

import com.nch.bookmanager.dto.UserPreferenceDto;
import com.nch.bookmanager.entity.Book;
import com.nch.bookmanager.entity.User;
import com.nch.bookmanager.repository.BookRepository;
import com.nch.bookmanager.repository.RentalRecordRepository;
import com.nch.bookmanager.repository.UserRepository;
import com.nch.bookmanager.utils.SimilarityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RecommendationCoreService {

    private final UserRepository userRepository;
    private final RentalService rentalService;
    private final RentalRecordRepository rentalRecordRepository;
    private final BookRepository bookRepository;

    private static final int NEIGHBOR_COUNT = 5; // 이웃 사용자 수
    private static final int RECOMMENDATION_LIMIT = 5; // 최종 추천 도서 수

    public RecommendationCoreService(UserRepository userRepository,
                                     RentalService rentalService,
                                     RentalRecordRepository rentalRecordRepository,
                                     BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
        this.rentalRecordRepository = rentalRecordRepository;
        this.bookRepository = bookRepository;
    }


    public List<Book> recommendByUserBased(String currentUsername) {

        // 1. 현재 사용자 선호 정보
        UserPreferenceDto currentUserPrefs = rentalService.getUserPreferences(currentUsername);
        List<User> allUsers = userRepository.findAll();

        // 2. 다른 사용자들과의 유사도 계산
        Map<String, Double> similarityScores = new HashMap<>();
        for (User otherUser : allUsers) {
            String otherUsername = otherUser.getUsername();
            if (otherUsername.equals(currentUsername)) continue;

            UserPreferenceDto otherUserPrefs = rentalService.getUserPreferences(otherUsername);
            double simScore = calculateCombinedCosineSimilarity(currentUserPrefs, otherUserPrefs);
            similarityScores.put(otherUsername, simScore);
        }

        // 3. 상위 N명 이웃 선정
        List<String> topNeighbors = similarityScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(NEIGHBOR_COUNT)
                .map(Map.Entry::getKey)
                .toList();

        if (topNeighbors.isEmpty()) return Collections.emptyList();

        // 4. 추천 후보 도서 선정
        Set<Long> myRentedBookIds = rentalRecordRepository.findRentalRecordsByUsername(currentUsername).stream()
                .map(record -> record.getBook().getId())
                .collect(Collectors.toSet());

        Set<Long> neighborRentedBookIds = new HashSet<>();
        for (String neighborUsername : topNeighbors) {
            rentalRecordRepository.findRentalRecordsByUsername(neighborUsername).stream()
                    .map(record -> record.getBook().getId())
                    .forEach(neighborRentedBookIds::add);
        }

        // 내가 읽은 책 제외
        neighborRentedBookIds.removeAll(myRentedBookIds);

        List<Long> finalIds = neighborRentedBookIds.stream()
                .limit(RECOMMENDATION_LIMIT)
                .toList();

        return bookRepository.findAllById(finalIds);
    }


    public List<Book> recommendByItemBased(String currentUsername) {

        // 1. 모든 도서 및 사용자가 이미 읽은 도서
        List<Book> allBooks = bookRepository.findAll();
        Set<Long> myRentedBookIds = rentalRecordRepository.findRentalRecordsByUsername(currentUsername).stream()
                .map(record -> record.getBook().getId())
                .collect(Collectors.toSet());

        // 2. 현재 사용자 선호 벡터 생성
        UserPreferenceDto currentUserPrefs = rentalService.getUserPreferences(currentUsername);
        Map<String, Long> userVector = getCombinedFeatureMap(currentUserPrefs);
        if (userVector.isEmpty()) return Collections.emptyList();

        // 3. 도서별 유사도 계산
        Map<Long, Double> similarityScores = new HashMap<>();
        for (Book book : allBooks) {
            if (myRentedBookIds.contains(book.getId())) continue;

            Map<String, Long> bookVector = getBookFeatureVector(book);
            double simScore = SimilarityUtil.calculateCosineSimilarity(userVector, bookVector);
            similarityScores.put(book.getId(), simScore);
        }

        // 4. 상위 N개 도서 선택
        List<Long> recommendedBookIds = similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(RECOMMENDATION_LIMIT)
                .map(Map.Entry::getKey)
                .toList();

        return bookRepository.findAllById(recommendedBookIds);
    }


    private double calculateCombinedCosineSimilarity(UserPreferenceDto userA, UserPreferenceDto userB) {
        double authorSim = SimilarityUtil.calculateCosineSimilarity(userA.getAuthorCounts(), userB.getAuthorCounts());
        double publisherSim = SimilarityUtil.calculateCosineSimilarity(userA.getPublisherCounts(), userB.getPublisherCounts());
        double kdcSim = SimilarityUtil.calculateCosineSimilarity(userA.getKdcCounts(), userB.getKdcCounts());
        return (authorSim + publisherSim + kdcSim) / 3.0;
    }


    private Map<String, Long> getCombinedFeatureMap(UserPreferenceDto prefs) {
        Map<String, Long> combined = new HashMap<>();
        if (prefs.getAuthorCounts() != null) combined.putAll(prefs.getAuthorCounts());
        if (prefs.getPublisherCounts() != null) combined.putAll(prefs.getPublisherCounts());
        if (prefs.getKdcCounts() != null) combined.putAll(prefs.getKdcCounts());
        return combined;
    }


    private Map<String, Long> getBookFeatureVector(Book book) {
        Map<String, Long> vector = new HashMap<>();
        if (book.getAuthor() != null && !book.getAuthor().trim().isEmpty()) vector.put(book.getAuthor(), 1L);
        if (book.getPublisher() != null && !book.getPublisher().trim().isEmpty()) vector.put(book.getPublisher(), 1L);
        if (book.getKdc() != null && !book.getKdc().trim().isEmpty()) vector.put(book.getKdc(), 1L);
        return vector;
    }

}
