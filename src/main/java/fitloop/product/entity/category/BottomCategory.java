package fitloop.product.entity.category;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import java.util.Arrays;

@Getter
public enum BottomCategory {
    // 신발
    ALL_SHOES("000", "신발_전체"),
    SNEAKERS("001", "스니커즈"),
    PADDED_SHOES("002", "패딩/퍼 신발"),
    BOOTS("003", "부츠/워커"),
    DRESS_SHOES("004", "구두"),
    SANDAL_SLIPPER("005", "샌들/슬리퍼"),
    SPORTS_SHOES("006", "스포츠화"),
    SHOE_ACCESSORIE("007", "신발용품"),
    ETC_SHOES("008", "신발_기타"),

    // 아우터
    ALL_OUTER("000", "아우터_전체"),
    SHORT_PUFFY_OUTER("001", "숏패딩/헤비 아우터"),
    MOUTON_FUR("002", "무스탕/퍼"),
    HOOD_ZIPUP("003", "후드 집업"),
    BLOUSON("004", "블루종"),
    LEATHER_RIDERS_JACKET("005", "레더/라이더스 재킷"),
    TRUCKER_JACKET("006", "트러거 재킷"),
    SUIT_BLAZER_JACKET("007", "슈트/블레이저 재킷"),
    CARDIGAN("008", "카디건"),
    ANORAK_JACKET("009", "아노락 재킷"),
    FLEECE("010", "폴리스/뽀글이"),
    TRAINING_JACKET("011", "트레이닝 재킷"),
    STADIUM_JACKET("012", "스타디움 재킷"),
    TRANSITIONAL_COAT("013", "환절기 코트"),
    WINTER_COAT("014", "겨울 코트"),
    LONG_PUFFY_OUTER("015", "롱패딩/헤비 아우터"),
    PADDED_VEST("016", "패딩 베스트"),
    ETC_OUTERWEAR("017", "아우터_기타"),

    // 상의
    ALL_TOP("000", "상의_전체"),
    KNIT_SWEATER("001", "니트/스웨터"),
    SWEATSHIRT("002", "맨투맨/스웨트"),
    HOODIE("003", "후드 티셔츠"),
    SHIRT_BLOUSE("004", "셔츠/블라우스"),
    PIQUE_COLLAR_TSHIRT("005", "피케/카라 티셔츠"),
    LONG_SLEEVE_TSHIRT("006", "긴소매 티셔츠"),
    SHORT_SLEEVE_TSHIRT("007", "반소매 티셔츠"),
    SLEEVELESS_TSHIRT("008", "민소매 티셔츠"),
    ETC_TOP("009", "상의_기타"),

    // 바지
    ALL_PANTS("000", "바지_전체"),
    DENIM_PANTS("001", "데님 팬츠"),
    JOGGER_PANTS("002", "트레이닝/조거 팬츠"),
    COTTON_PANTS("003", "코튼 팬츠"),
    SUIT_SLACKS("004", "슈트 팬츠/슬랙스"),
    SHORT_PANTS("005", "숏 팬츠"),
    LEGGINGS("006", "레깅스"),
    JUMPSUIT_OVERALL("007", "점프 슈트/오버올"),
    ETC_PANT("008", "바지_기타"),

    // 원피스
    ALL_DRESS("000", "원피스_전체"),
    MINI_DRESS("001", "미니원피스"),
    MIDI_DRESS("002", "미디원피스"),
    MAXI_DRESS("003", "맥시원피스"),
    ETC_DRESS("004", "원피스_기타"),

    // 스커트
    ALL_SKIRT("000", "스커트_전체"),
    MINI_SKIRT("001", "미니스커트"),
    MIDI_SKIRT("002", "미디스커트"),
    LONG_SKIRT("003", "롱스커트"),
    ETC_SKIRT("004", "스커트_기타"),

    // 가방
    ALL_BAG("000", "가방_전체"),
    MESSENGER_CROSS_BAG("001", "메신저/크로스 백"),
    SHOULDER_BAG("002", "숄더백"),
    BACKPACK("003", "백팩"),
    TOTE_BAG("004", "토트백"),
    ECO_BAG("005", "에코백"),
    BOSTON_DUFFLE_BAG("006", "보스턴/더블백"),
    WAIST_BAG("007", "웨이스트 백"),
    POUCH_BAG("008", "파우치 백"),
    BRIEFCASE("009", "브리프 케이스"),
    CARRIER("010", "캐리어"),
    BAG_ACCESSORIES("011", "가방소품"),
    WALLET_MONEY_CLIP("012", "지갑/머니클립"),
    CLUTCH_BAG("013", "클러치 백"),
    ETC_BAG("014", "가방_기타"),

    // 패션소품
    ALL_FASHION_ACCESSORY("000", "패션소품_전체"),
    HAT("001", "모자"),
    SCARF("002", "머플러"),
    JEWELRY("003", "주얼리"),
    SOCKS_LEGWEAR("004", "양말/레그웨어"),
    SUNGLASSES_GLASSES("005", "선글라스/안경태"),
    ACCESSORY("006", "액세서리"),
    WATCH("007", "시계"),
    BELT("008", "벨트"),
    ETC_FASHION_ACCESSORY("009", "패션소품_기타");

    private final String code;
    private final String description;

    BottomCategory(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    public static BottomCategory fromDescription(String description) {
        return Arrays.stream(BottomCategory.values())
                .filter(category -> category.description.equals(description))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 이름에 맞는 소카테고리는 없습니다: " + description));
    }

    public static BottomCategory fromCode(String code) {
        return Arrays.stream(BottomCategory.values())
                .filter(category -> category.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 코드에 맞는 중카테고리는 없습니다: " + code));
    }
}