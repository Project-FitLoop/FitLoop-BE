package fitloop.product.entity.category;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import java.util.Arrays;

@Getter
public enum BottomCategory {
    // 신발
    SNEAKERS("스니커즈"),
    PADDED_SHOES("패딩/퍼 신발"),
    BOOTS("부츠/워커"),
    DRESS_SHOES("구두"),
    SANDAL_SLIPPER("샌들/슬리퍼"),
    SPORTS_SHOES("스포츠화"),
    SHOE_ACCESSORIE("신발용품"),


    // 아우터
    SHORT_PUFFY_OUTER("숏패딩/헤비 아우터"),
    MOUTON_FUR("무스탕/퍼"),
    HOOD_ZIPUP("후드 집업"),
    BLOUSON("블루종"),
    LEATHER_RIDERS_JACKET("레더/라이더스 재킷"),
    TRUCKER_JACKET("트러거 재킷"),
    SUIT_BLAZER_JACKET("슈트/블레이저 재킷"),
    CARDIGAN("카디건"),
    ANORAK_JACKET("아노락 재킷"),
    FLEECE("폴리스/뽀글이"),
    TRAINING_JACKET("트레이닝 재킷"),
    STADIUM_JACKET("스타디움 재킷"),
    TRANSITIONAL_COAT("환절기 코트"),
    WINTER_COAT("겨울 코트"),
    LONG_PUFFY_OUTER("롱패딩/헤비 아우터"),
    PADDED_VEST("패딩 베스트"),

    // 상의
    KNIT_SWEATER("니트/스웨터"),
    SWEATSHIRT("맨투맨/스웨트"),
    HOODIE("후드 티셔츠"),
    SHIRT_BLOUSE("셔츠/블라우스"),
    PIQUE_COLLAR_TSHIRT("피케/카라 티셔츠"),
    LONG_SLEEVE_TSHIRT("긴소매 티셔츠"),
    SHORT_SLEEVE_TSHIRT("반소매 티셔츠"),
    SLEEVELESS_TSHIRT("민소매 티셔츠"),
    OTHER_TOPS("기타 상의"),

    // 바지
    DENIM_PANTS("데님 팬츠"),
    JOGGER_PANTS("트레이닝/조거 팬츠"),
    COTTON_PANTS("코튼 팬츠"),
    SUIT_SLACKS("슈트 팬츠/슬랙스"),
    SHORT_PANTS("숏 팬츠"),
    LEGGINGS("레깅스"),
    JUMPSUIT_OVERALL("점프 슈트/오버올"),
    OTHER_BOTTOMS("기타 하의"),

    // 원피스
    MIDI_DRESS("미디원피스"),
    MAXI_DRESS("맥시원피스"),

    // 스커트
    MIDI_SKIRT("미디스커트"),
    LONG_SKIRT("롱스커트"),

    // 가방
    MESSENGER_CROSS_BAG("메신저/크로스 백"),
    SHOULDER_BAG("숄더백"),
    BACKPACK("백팩"),
    TOTE_BAG("토트백"),
    ECO_BAG("에코백"),
    BOSTON_DUFFLE_BAG("보스턴/더블백"),
    WAIST_BAG("웨이스트 백"),
    POUCH_BAG("파우치 백"),
    BRIEFCASE("브리프 케이스"),
    CARRIER("캐리어"),
    BAG_ACCESSORIES("가방소품"),
    WALLET_MONEY_CLIP("지갑/머니클립"),
    CLUTCH_BAG("클러치 백"),

    // 패션소품
    HAT("모자"),
    SCARF("머플러"),
    JEWELRY("주얼리"),
    SOCKS_LEGWEAR("양말/레그웨어"),
    SUNGLASSES_GLASSES("선글라스/안경태"),
    ACCESSORY("액세서리"),
    WATCH("시계"),
    BELT("벨트"),

    //기타
    ETC_SHOES("신발_기타"),
    ETC_OUTERWEAR("아우터_기타"),
    ETC_TOP("상의_기타"),
    ETC_PANT("바지_기타"),
    ETC_DRESS("원피스_기타"),
    ETC_SKIRT("스커트_기타"),
    ETC_BAG("가방_기타"),
    ETC_FASHION_ACCESSORY("패션소품_기타");

    private final String description;

    BottomCategory(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    public static BottomCategory from(String description) {
        return Arrays.stream(BottomCategory.values())
                .filter(category -> category.description.equals(description))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 이름에 맞는 소카테고리는 없습니다: " + description));
    }
}