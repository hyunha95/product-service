# 추천 상품 API 가이드

## 개요

이 프로젝트는 **Item-based Collaborative Filtering (상품 기반 협업 필터링)**을 사용한 개인화 추천 시스템입니다.

### 주요 기능

- **개인화 추천**: 사용자의 상호작용 이력을 기반으로 맞춤 상품 추천
- **유사 상품 추천**: 특정 상품과 유사한 다른 상품 추천
- **Redis 캐싱**: 추천 결과와 유사도 매트릭스를 캐시하여 성능 최적화
- **실시간 상호작용 추적**: 조회, 장바구니, 구매 이벤트 기록

### 기술 스택

- Spring Boot 3.5.10
- PostgreSQL (상호작용 데이터 저장)
- Redis (캐싱 및 성능 최적화)
- Java 21

---

## 빠른 시작

### 1. 환경 설정

```bash
# Docker로 PostgreSQL과 Redis 시작
docker compose up -d

# 애플리케이션 실행
./gradlew bootRun
```

서버가 **http://localhost:9090**에서 실행됩니다.

### 2. 샘플 데이터 로드 (선택사항)

개발 프로필로 실행하면 자동으로 샘플 데이터가 로드됩니다:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

---

## API 엔드포인트

### 1. 사용자 상호작용 기록

사용자가 상품을 조회, 장바구니 추가, 구매했을 때 호출합니다.

**POST** `/api/interactions`

**Request Body:**
```json
{
  "userId": "user123",
  "productId": 1,
  "interactionType": "VIEW"  // VIEW, CART, PURCHASE
}
```

**상호작용 타입별 가중치:**
- `VIEW`: 1.0 (조회)
- `CART`: 2.0 (장바구니)
- `PURCHASE`: 3.0 (구매)

**Response:** `201 Created`

**예제:**
```bash
curl -X POST http://localhost:9090/api/interactions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "productId": 1,
    "interactionType": "VIEW"
  }'
```

---

### 2. 개인화 추천 조회

특정 사용자에게 맞춤 상품을 추천합니다.

**GET** `/api/recommendations/personalized?userId={userId}&limit={limit}`

**Query Parameters:**
- `userId` (required): 사용자 ID
- `limit` (optional, default=10): 추천 개수

**Response:**
```json
[
  {
    "productId": 5,
    "score": 2.8743,
    "reason": "Based on similar products"
  },
  {
    "productId": 3,
    "score": 2.1234,
    "reason": "Based on similar products"
  }
]
```

**예제:**
```bash
curl http://localhost:9090/api/recommendations/personalized?userId=user123&limit=5
```

**로직:**
1. 사용자가 이미 상호작용한 상품 목록 조회
2. 각 상품과 유사한 다른 상품들을 찾음 (코사인 유사도 기반)
3. 상호작용 가중치와 유사도를 곱해서 점수 계산
4. 상위 N개 반환

**캐싱:**
- 결과는 Redis에 1시간 동안 캐시됩니다
- 캐시 키: `rec:user:{userId}`

---

### 3. 유사 상품 추천

특정 상품과 유사한 다른 상품을 추천합니다.

**GET** `/api/recommendations/similar/{productId}?limit={limit}`

**Path Parameters:**
- `productId` (required): 기준 상품 ID

**Query Parameters:**
- `limit` (optional, default=10): 추천 개수

**Response:**
```json
[
  {
    "productId": 2,
    "score": 0.8567,
    "reason": "Similar product"
  },
  {
    "productId": 4,
    "score": 0.7234,
    "reason": "Similar product"
  }
]
```

**예제:**
```bash
curl http://localhost:9090/api/recommendations/similar/1?limit=5
```

**로직:**
1. 상품 간 유사도 매트릭스에서 해당 상품의 유사 상품 조회
2. 유사도 점수가 높은 순으로 정렬
3. 상위 N개 반환

**캐싱:**
- 결과는 Redis에 24시간 동안 캐시됩니다
- 캐시 키: `rec:similar:{productId}`

---

### 4. 추천 모델 재빌드

새로운 상호작용 데이터를 반영하여 추천 모델을 다시 계산합니다.

**POST** `/api/recommendations/rebuild`

**Response:** `200 OK`

**예제:**
```bash
curl -X POST http://localhost:9090/api/recommendations/rebuild
```

**언제 사용하나요?**
- 배치 작업으로 주기적으로 실행 (예: 매일 새벽)
- 많은 상호작용 데이터가 쌓였을 때
- 추천 품질이 떨어졌다고 판단될 때

**처리 과정:**
1. 모든 사용자-상품 상호작용 데이터 로드
2. 상품 간 코사인 유사도 계산
3. 유사도 매트릭스를 메모리 및 Redis에 저장

**주의:**
- 데이터가 많을 경우 시간이 오래 걸릴 수 있습니다
- 프로덕션에서는 스케줄러로 자동 실행 권장

---

## 추천 알고리즘 상세

### Item-based Collaborative Filtering

**1. 코사인 유사도 계산**

두 상품 간 유사도는 다음 공식으로 계산됩니다:

```
similarity(A, B) = (A · B) / (||A|| × ||B||)
```

- A, B: 각 상품을 상호작용한 사용자들의 가중치 벡터
- 유사도 범위: 0.0 (완전 다름) ~ 1.0 (완전 동일)

**2. 추천 점수 계산**

사용자가 본 각 상품에 대해:
```
score(product) = Σ(similarity × interaction_weight)
```

**예시:**
```
사용자가 상품 A(노트북)를 봄 (weight=1.0)
  - 상품 B(마우스)와 유사도 0.8 → 점수 0.8
  - 상품 C(모니터)와 유사도 0.7 → 점수 0.7

사용자가 상품 D(키보드)를 구매 (weight=3.0)
  - 상품 B(마우스)와 유사도 0.6 → 점수 1.8
  - 상품 E(헤드셋)와 유사도 0.5 → 점수 1.5

최종 추천:
  1. 마우스 (0.8 + 1.8 = 2.6)
  2. 헤드셋 (1.5)
  3. 모니터 (0.7)
```

---

## 성능 최적화

### Redis 캐싱 전략

1. **사용자 추천 캐시**
   - TTL: 1시간
   - 키 패턴: `rec:user:{userId}`
   - 이유: 사용자 행동은 단기간에 크게 변하지 않음

2. **유사 상품 캐시**
   - TTL: 24시간
   - 키 패턴: `rec:similar:{productId}`
   - 이유: 상품 간 유사도는 안정적

3. **유사도 매트릭스 캐시**
   - TTL: 24시간
   - 키: `rec:matrix:similarity`
   - 이유: 모델 재빌드 후 전체 애플리케이션에서 공유

### 설정 커스터마이징

`application.yaml`에서 캐시 TTL을 조정할 수 있습니다:

```yaml
recommendation:
  similarity:
    cache-ttl: 86400  # 24시간 (초 단위)
  user-recommendations:
    cache-ttl: 3600   # 1시간 (초 단위)
  max-recommendations: 10  # 최대 추천 개수
```

---

## 통합 예제

### 전체 추천 워크플로우

```bash
# 1. 사용자가 상품 1을 조회
curl -X POST http://localhost:9090/api/interactions \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123", "productId": 1, "interactionType": "VIEW"}'

# 2. 사용자가 상품 3을 장바구니에 추가
curl -X POST http://localhost:9090/api/interactions \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123", "productId": 3, "interactionType": "CART"}'

# 3. 사용자가 상품 3을 구매
curl -X POST http://localhost:9090/api/interactions \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123", "productId": 3, "interactionType": "PURCHASE"}'

# 4. 개인화 추천 조회
curl http://localhost:9090/api/recommendations/personalized?userId=user123&limit=5

# 5. 상품 3과 유사한 상품 조회
curl http://localhost:9090/api/recommendations/similar/3?limit=5

# 6. 추천 모델 재빌드 (배치 작업)
curl -X POST http://localhost:9090/api/recommendations/rebuild
```

---

## 프론트엔드 통합 가이드

### React 예제

```typescript
// 상호작용 기록
const recordInteraction = async (productId: number, type: 'VIEW' | 'CART' | 'PURCHASE') => {
  await fetch('http://localhost:9090/api/interactions', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      userId: getCurrentUserId(),
      productId,
      interactionType: type
    })
  });
};

// 개인화 추천 조회
const getRecommendations = async (userId: string, limit = 10) => {
  const res = await fetch(
    `http://localhost:9090/api/recommendations/personalized?userId=${userId}&limit=${limit}`
  );
  return res.json();
};

// 유사 상품 조회
const getSimilarProducts = async (productId: number, limit = 5) => {
  const res = await fetch(
    `http://localhost:9090/api/recommendations/similar/${productId}?limit=${limit}`
  );
  return res.json();
};

// 사용 예시
useEffect(() => {
  // 상품 페이지 진입 시 조회 기록
  recordInteraction(productId, 'VIEW');

  // 유사 상품 추천 표시
  getSimilarProducts(productId, 5).then(setRecommendations);
}, [productId]);
```

---

## 모니터링 및 디버깅

### 로그 확인

애플리케이션 로그에서 추천 동작을 확인할 수 있습니다:

```
2025-02-13 10:30:15 INFO  RecommendationEngine - Generating personalized recommendations for user: user123
2025-02-13 10:30:15 INFO  RecommendationEngine - Found cached recommendations for user: user123
2025-02-13 10:30:15 INFO  RecommendationEngine - Generated 5 recommendations for user: user123
```

### Redis 데이터 확인

```bash
# Redis CLI 접속
docker exec -it <redis-container-id> redis-cli

# 캐시된 키 확인
KEYS rec:*

# 특정 사용자 추천 조회
GET rec:user:user123

# 유사도 매트릭스 확인
GET rec:matrix:similarity
```

### 데이터베이스 확인

```sql
-- 상호작용 데이터 확인
SELECT * FROM user_product_interactions
WHERE user_id = 'user123'
ORDER BY created_at DESC;

-- 상품별 상호작용 통계
SELECT product_id, interaction_type, COUNT(*)
FROM user_product_interactions
GROUP BY product_id, interaction_type;
```

---

## 프로덕션 배포 시 고려사항

### 1. 스케줄링

추천 모델을 주기적으로 재빌드하도록 스케줄러를 설정하세요:

```java
@Scheduled(cron = "0 0 2 * * *")  // 매일 새벽 2시
public void rebuildRecommendations() {
    recommendationEngine.rebuildRecommendationModel();
}
```

### 2. 성능 튜닝

- 유사도 임계값 조정 (현재 0.1, 더 높이면 메모리 절약)
- 캐시 TTL 조정 (트래픽 패턴에 따라)
- Redis 메모리 모니터링

### 3. 확장성

- Redis Cluster 구성 (대용량 트래픽)
- 읽기 전용 복제본 추가 (PostgreSQL)
- 추천 모델 빌드를 별도 서비스로 분리

---

## 문제 해결

### Q: 추천 결과가 비어있어요
- 사용자 상호작용 데이터가 충분한지 확인
- `POST /api/recommendations/rebuild`로 모델 재빌드
- 샘플 데이터 로드: `--spring.profiles.active=dev`로 실행

### Q: 추천이 너무 느려요
- Redis가 실행 중인지 확인: `docker ps`
- 캐시가 작동하는지 로그 확인
- 유사도 매트릭스가 너무 크면 임계값 조정

### Q: 추천 품질이 낮아요
- 더 많은 상호작용 데이터 수집
- 가중치 조정 (InteractionType enum)
- 모델 재빌드 주기 단축

---

## 라이선스

MIT License
