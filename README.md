# Product Recommendation Service

**개인화 추천 시스템** - Item-based Collaborative Filtering을 사용한 상품 추천 마이크로서비스

## 🚀 주요 기능

- **개인화 추천**: 사용자의 상호작용 이력 기반 맞춤 상품 추천
- **유사 상품 추천**: 특정 상품과 유사한 다른 상품 추천
- **실시간 상호작용 추적**: 조회, 장바구니, 구매 이벤트 기록
- **Redis 캐싱**: 추천 결과 캐시로 성능 최적화
- **협업 필터링**: 코사인 유사도 기반 Item-based CF 알고리즘

## 📚 기술 스택

- **Spring Boot** 3.5.10
- **Java** 21
- **PostgreSQL** - 상호작용 데이터 저장
- **Redis** - 캐싱 및 성능 최적화
- **Hibernate/JPA** - ORM
- **Lombok** - 보일러플레이트 제거

## 📖 아키텍처

DDD (Domain-Driven Design) 4계층 구조:

```
src/main/java/kr/co/haulic/product/
├── product/              # 상품 도메인
│   ├── domain/          # Product 엔티티, 리포지토리 인터페이스
│   └── infrastructure/  # JPA 구현
├── interaction/         # 사용자-상품 상호작용 도메인
│   ├── domain/          # UserProductInteraction 엔티티
│   ├── application/     # 유스케이스 (상호작용 기록)
│   ├── infrastructure/  # JPA 구현
│   └── presentation/    # REST 컨트롤러
└── recommendation/      # 추천 도메인
    ├── domain/          # Recommendation, RecommendationEngine
    ├── application/     # 유스케이스 (추천 조회)
    ├── infrastructure/  # 협업 필터링 구현, Redis 캐시
    └── presentation/    # REST 컨트롤러
```

## 🏃 빠른 시작

### 1. 환경 설정

```bash
# PostgreSQL & Redis 시작
docker compose up -d

# 애플리케이션 실행 (샘플 데이터 포함)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

서버 주소: **http://localhost:9090**

### 2. 샘플 데이터

`dev` 프로필로 실행하면 자동으로 다음이 생성됩니다:
- 8개 상품 (전자제품, 가구)
- 5명의 사용자 (user1 ~ user5)
- 25개의 상호작용 기록

## 📡 API 엔드포인트

### 개인화 추천 조회

```bash
GET /api/recommendations/personalized?userId={userId}&limit={limit}

# 예시
curl "http://localhost:9090/api/recommendations/personalized?userId=user1&limit=5"

# 응답
[
  {
    "productId": 3,
    "score": 19.47,
    "reason": "Based on similar products"
  },
  {
    "productId": 2,
    "score": 13.0,
    "reason": "Based on similar products"
  }
]
```

### 유사 상품 추천

```bash
GET /api/recommendations/similar/{productId}?limit={limit}

# 예시
curl "http://localhost:9090/api/recommendations/similar/1?limit=5"

# 응답
[
  {
    "productId": 2,
    "score": 0.85,
    "reason": "Similar product"
  }
]
```

### 상호작용 기록

```bash
POST /api/interactions

# 예시
curl -X POST http://localhost:9090/api/interactions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "productId": 1,
    "interactionType": "VIEW"
  }'

# 상호작용 타입
# - VIEW: 조회 (가중치 1.0)
# - CART: 장바구니 (가중치 2.0)
# - PURCHASE: 구매 (가중치 3.0)
```

### 추천 모델 재빌드

```bash
POST /api/recommendations/rebuild

# 예시
curl -X POST http://localhost:9090/api/recommendations/rebuild
```

## 🧠 추천 알고리즘

### Item-based Collaborative Filtering

1. **유사도 계산**: 코사인 유사도를 사용하여 상품 간 유사성 측정
2. **추천 점수**: `score = Σ(similarity × interaction_weight)`
3. **캐싱**: Redis로 결과를 캐시하여 응답 속도 향상

**예시:**
```
사용자가 "노트북" 조회 (weight=1.0)
  → 유사한 "마우스" (similarity 0.8) → 점수 0.8

사용자가 "키보드" 구매 (weight=3.0)
  → 유사한 "마우스" (similarity 0.6) → 점수 1.8

최종 "마우스" 추천 점수: 0.8 + 1.8 = 2.6
```

## ⚙️ 설정

### application.yaml

```yaml
# 추천 시스템 설정
recommendation:
  similarity:
    cache-ttl: 86400  # 유사도 매트릭스 캐시 (24시간)
  user-recommendations:
    cache-ttl: 3600   # 사용자 추천 캐시 (1시간)
  max-recommendations: 10  # 최대 추천 개수
```

### 캐싱 전략

- **사용자 추천**: 1시간 TTL (키: `rec:user:{userId}`)
- **유사 상품**: 24시간 TTL (키: `rec:similar:{productId}`)
- **유사도 매트릭스**: 24시간 TTL (키: `rec:matrix:similarity`)

## 📊 데이터베이스 스키마

### products

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | 상품 ID (PK) |
| name | VARCHAR | 상품명 |
| description | TEXT | 상품 설명 |
| price | DECIMAL(10,2) | 가격 |
| category_id | VARCHAR | 카테고리 ID |
| view_count | INT | 조회수 |
| purchase_count | INT | 구매수 |

### user_product_interactions

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | 상호작용 ID (PK) |
| user_id | VARCHAR | 사용자 ID (인덱스) |
| product_id | BIGINT | 상품 ID (인덱스) |
| interaction_type | ENUM | VIEW/CART/PURCHASE |
| weight | DOUBLE | 가중치 |
| created_at | TIMESTAMP | 생성일시 |

## 🔧 개발 가이드

### 테스트

```bash
# 전체 테스트
./gradlew test

# 특정 테스트
./gradlew test --tests "kr.co.haulic.product.*"
```

### 빌드

```bash
# 빌드
./gradlew build

# 클린 빌드
./gradlew clean build
```

### 프로덕션 실행

```bash
# dev 프로필 없이 실행 (샘플 데이터 X)
./gradlew bootRun
```

## 📈 성능 최적화

- **인덱스**: user_id, product_id, (user_id, product_id) 복합 인덱스
- **Redis 캐싱**: 반복적인 추천 요청 캐시
- **메모리 캐싱**: 유사도 매트릭스 메모리 보관
- **배치 처리**: 주기적 모델 재빌드로 실시간 성능 유지

## 🚀 프로덕션 배포

### 스케줄러 설정 (권장)

```java
@Scheduled(cron = "0 0 2 * * *")  // 매일 새벽 2시
public void rebuildRecommendations() {
    recommendationEngine.rebuildRecommendationModel();
}
```

### 환경변수

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/product
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=pass
SPRING_DATA_REDIS_HOST=redis-host
SPRING_DATA_REDIS_PORT=6379
```

## 📝 추가 문서

- **[API 가이드](API_GUIDE.md)**: 전체 API 문서 및 사용 예제
- **[CLAUDE.md](CLAUDE.md)**: 프로젝트 구조 및 개발 가이드

## 🐛 문제 해결

**Q: 추천 결과가 없어요**
- 상호작용 데이터가 충분한지 확인
- `/api/recommendations/rebuild`로 모델 재빌드

**Q: Redis 연결 오류**
- `docker ps`로 Redis 컨테이너 확인
- `docker compose up -d`로 재시작

**Q: 추천 품질이 낮아요**
- 더 많은 상호작용 데이터 수집
- 가중치 조정 (InteractionType enum)
- 모델 재빌드 주기 조정

## 📄 라이선스

MIT License

---

Made with ❤️ using Spring Boot & Redis
