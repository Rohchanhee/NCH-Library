package com.nch.bookmanager.utils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class SimilarityUtil {

    /**
     * 두 집합 A와 B 간의 자카드 유사도를 계산합니다.
     * J(A, B) = |A ∩ B| / |A ∪ B|
     *
     * @param setA 첫 번째 집합 (사용자 또는 도서 특징)
     * @param setB 두 번째 집합 (사용자 또는 도서 특징)
     * @return 0.0 ~ 1.0 사이의 자카드 유사도 값
     */
    public static double calculateJaccardSimilarity(Set<String> setA, Set<String> setB) {

        // 1. 공통 요소 확인을 위해 집합이 비어있는지 확인
        if (setA == null || setA.isEmpty() || setB == null || setB.isEmpty()) {
            return 0.0;
        }

        // 2. 교집합 (Intersection) 계산
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB); // setA가 setB와 공통된 요소만 남김

        // 3. 합집합 (Union) 계산
        Set<String> union = new HashSet<>(setA);
        union.addAll(setB); // setA에 setB의 모든 요소를 추가

        // 4. 자카드 유사도 계산: 교집합 크기 / 합집합 크기
        // 합집합 크기가 0이면 (두 집합 모두 비어있을 때) 0을 반환합니다. (1번에서 처리됨)
        if (union.isEmpty()) {
            return 0.0;
        }

        double intersectionSize = intersection.size();
        double unionSize = union.size();

        return intersectionSize / unionSize;
    }





    /**
     * 두 벡터 (Map<특징, 빈도수>) 간의 코사인 유사도를 계산합니다.
     * 특징(Key)을 기준으로 두 벡터를 비교하며 내적과 크기를 계산합니다.
     * * @param vectorA 첫 번째 벡터 (Map<String, Long>)
     * @param vectorB 두 번째 벡터 (Map<String, Long>)
     * @return -1.0 ~ 1.0 사이의 코사인 유사도 값 (0.0 이상으로 나올 가능성이 높음)
     */
    public static double calculateCosineSimilarity(Map<String, Long> vectorA, Map<String, Long> vectorB) {
        if (vectorA == null || vectorA.isEmpty() || vectorB == null || vectorB.isEmpty()) {
            return 0.0;
        }

        double dotProduct = 0.0; // 내적 (Numerator)
        double magnitudeA = 0.0; // 벡터 A의 크기 제곱 (Denominator part 1)
        double magnitudeB = 0.0; // 벡터 B의 크기 제곱 (Denominator part 2)

        // 1. 모든 특징(Key)을 통합하여 비교 기준을 만듭니다.
        Set<String> allFeatures = new HashSet<>(vectorA.keySet());
        allFeatures.addAll(vectorB.keySet());

        for (String feature : allFeatures) {
            // A 벡터의 값 (해당 특징의 빈도수, 없으면 0)
            long a = vectorA.getOrDefault(feature, 0L);
            // B 벡터의 값 (해당 특징의 빈도수, 없으면 0)
            long b = vectorB.getOrDefault(feature, 0L);

            // 2. 내적 계산 (dotProduct): Σ(Ai * Bi)
            dotProduct += (double) a * b;

            // 3. 크기 제곱 계산 (Magnitude Squared): Σ(Ai^2) + Σ(Bi^2)
            // (a와 b가 0L인 경우에도 모든 특징에 대해 크기 제곱을 계산해야 합니다.)
            if (vectorA.containsKey(feature)) {
                magnitudeA += (double) a * a;
            }
            if (vectorB.containsKey(feature)) {
                magnitudeB += (double) b * b;
            }
        }

        // 만약 두 벡터 모두 크기가 0이라면 (특징이 겹치지 않는다면) 유사도는 0
        if (magnitudeA == 0.0 || magnitudeB == 0.0) {
            return 0.0;
        }

        // 4. 코사인 유사도 계산: 내적 / (크기 A * 크기 B)
        // ||A|| * ||B|| = sqrt(MagnitudeA) * sqrt(MagnitudeB)
        double denominator = Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB);

        return dotProduct / denominator;
    }
}