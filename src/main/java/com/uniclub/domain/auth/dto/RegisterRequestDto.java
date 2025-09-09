package com.uniclub.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "회원가입 요청 DTO")
@Getter
@NoArgsConstructor
public class RegisterRequestDto {

    @Schema(description = "학번", example = "202012345")
    @NotBlank(message = "학번을 입력해주세요.")
    private String studentId;

    @Schema(description = "사용자 이름", example = "홍길동")
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @Schema(description = "사용자 전공", example = "/*\n" +
            "        대학\n" +
            "     */\n" +
            "\n" +
            "    // 인문대학\n" +
            "    KOREAN_LANGUAGE_LITERATURE(\"국어국문학과\"),\n" +
            "    ENGLISH_LANGUAGE_LITERATURE(\"영어영문학과\"),\n" +
            "    JAPANESE_REGIONAL_CULTURE(\"일본지역문화학과\"),\n" +
            "    CHINESE_LANGUAGE_LITERATURE(\"중어중국학과\"),\n" +
            "    GERMAN_LANGUAGE_LITERATURE(\"독어독문학과\"),\n" +
            "    FRENCH_LANGUAGE_LITERATURE(\"불어불문학과\"),\n" +
            "\n" +
            "    // 자연과학대학\n" +
            "    MATHEMATICS(\"수학과\"),\n" +
            "    PHYSICS(\"물리학과\"),\n" +
            "    CHEMISTRY(\"화학과\"),\n" +
            "    MARINE_SCIENCE(\"해양학과\"),\n" +
            "    FASHION_INDUSTRY(\"패션산업학과\"),\n" +
            "\n" +
            "    // 사회과학대학\n" +
            "    SOCIAL_WELFARE(\"사회복지학과\"),\n" +
            "    MEDIA_COMMUNICATION(\"미디어커뮤니케이션학과\"),\n" +
            "    LIBRARY_INFORMATION_SCIENCE(\"문헌정보학과\"),\n" +
            "    CREATIVE_TALENT_DEVELOPMENT(\"창의인재개발학과\"),\n" +
            "\n" +
            "    // 글로벌정경대학\n" +
            "    PUBLIC_ADMINISTRATION(\"행정학과\"),\n" +
            "    POLITICAL_SCIENCE_DIPLOMACY(\"정치외교학과\"),\n" +
            "    ECONOMICS(\"경제학과\"),\n" +
            "    TRADE(\"무역학부\"),\n" +
            "    CONSUMER_STUDIES(\"소비자학과\"),\n" +
            "\n" +
            "    // 공과대학\n" +
            "    MECHANICAL_ENGINEERING(\"기계공학과\"),\n" +
            "    ELECTRICAL_ENGINEERING(\"전기공학과\"),\n" +
            "    ELECTRONIC_ENGINEERING(\"전자공학과\"),\n" +
            "    INDUSTRIAL_MANAGEMENT_ENGINEERING(\"산업경영공학과\"),\n" +
            "    MATERIALS_SCIENCE_ENGINEERING(\"신소재공학과\"),\n" +
            "    SAFETY_ENGINEERING(\"안전공학과\"),\n" +
            "    ENERGY_CHEMICAL_ENGINEERING(\"에너지화학공학과\"),\n" +
            "    BIO_ROBOT_SYSTEM_ENGINEERING(\"바이오-로봇 시스템 공학과\"),\n" +
            "\n" +
            "    // 정보기술대학\n" +
            "    COMPUTER_ENGINEERING(\"컴퓨터공학부\"),\n" +
            "    INFORMATION_COMMUNICATION_ENGINEERING(\"정보통신공학과\"),\n" +
            "    EMBEDDED_SYSTEM_ENGINEERING(\"임베디드시스템공학과\"),\n" +
            "\n" +
            "    // 경영대학\n" +
            "    BUSINESS_ADMINISTRATION(\"경영학부\"),\n" +
            "    DATA_SCIENCE(\"데이터과학과\"),\n" +
            "    TAXATION_ACCOUNTING(\"세무회계학과\"),\n" +
            "    TECHNO_MANAGEMENT(\"테크노경영학과\"),\n" +
            "\n" +
            "    // 예술체육대학\n" +
            "    KOREAN_PAINTING(\"한국화전공(조형예술학부)\"),\n" +
            "    WESTERN_PAINTING(\"서양화전공(조형예술학부)\"),\n" +
            "    DESIGN(\"디자인학부\"),\n" +
            "    PERFORMING_ARTS(\"공연예술학과\"),\n" +
            "    SPORTS_SCIENCE(\"스포츠과학부\"),\n" +
            "    EXERCISE_HEALTH(\"운동건강학부\"),\n" +
            "\n" +
            "    // 사범대학\n" +
            "    KOREAN_EDUCATION(\"국어교육과\"),\n" +
            "    ENGLISH_EDUCATION(\"영어교육과\"),\n" +
            "    JAPANESE_EDUCATION(\"일어교육과\"),\n" +
            "    MATHEMATICS_EDUCATION(\"수학교육과\"),\n" +
            "    PHYSICAL_EDUCATION(\"체육교육과\"),\n" +
            "    EARLY_CHILDHOOD_EDUCATION(\"유아교육과\"),\n" +
            "    HISTORY_EDUCATION(\"역사교육과\"),\n" +
            "    ETHICS_EDUCATION(\"윤리교육과\"),\n" +
            "\n" +
            "    // 도시과학대학\n" +
            "    URBAN_ADMINISTRATION(\"도시행정학과\"),\n" +
            "    CONSTRUCTION_ENVIRONMENTAL_ENGINEERING(\"건설환경공학\"),\n" +
            "    ENVIRONMENTAL_ENGINEERING(\"환경공학\"),\n" +
            "    URBAN_ENGINEERING(\"도시공학과\"),\n" +
            "    ARCHITECTURAL_ENGINEERING(\"건축공학\"),\n" +
            "    URBAN_ARCHITECTURE(\"도시건축학\"),\n" +
            "\n" +
            "    // 생명과학기술대학\n" +
            "    LIFE_SCIENCE(\"생명과학부(생명과학전공)\"),\n" +
            "    MOLECULAR_LIFE_SCIENCE(\"생명과학부(분자의생명전공)\"),\n" +
            "    BIOTECHNOLOGY(\"생명공학부(생명공학전공)\"),\n" +
            "    NANO_BIOTECHNOLOGY(\"생명공학부(나노바이오공학전공)\"),\n" +
            "\n" +
            "    // 융합자유전공대학\n" +
            "    FREE_MAJOR(\"자유전공학부\"),\n" +
            "    INTERNATIONAL_FREE_MAJOR(\"국제자유전공학부\"),\n" +
            "    CONVERGENCE(\"융합학부\"),\n" +
            "\n" +
            "    // 동북아국제통상학부\n" +
            "    NORTHEAST_ASIA_INTERNATIONAL_COMMERCE(\"동북아국제통상전공\"),\n" +
            "    SMART_LOGISTICS_ENGINEERING(\"스마트물류공학전공\"),\n" +
            "    IBE(\"IBE전공\"),\n" +
            "\n" +
            "    // 법학부 (독립학부)\n" +
            "    LAW(\"법학부\"),\n" +
            "\n" +
            "\n" +
            "    /*\n" +
            "        대학원\n" +
            "     */\n" +
            "\n" +
            "    //일반 대학원\n" +
            "\n" +
            "    // 인문사회계열\n" +
            "    KOREAN_LANGUAGE_EDUCATION(\"한국어교육학과\"),\n" +
            "    EDUCATION(\"교육학과\"),\n" +
            "    ETHICS(\"윤리학과\"),\n" +
            "    CHINESE_STUDIES(\"중국학과\"),\n" +
            "    LAW_DEPARTMENT(\"법학과\"),\n" +
            "    BUSINESS_ADMINISTRATION_DEPARTMENT(\"경영학과\"),\n" +
            "    TRADE_DEPARTMENT(\"무역학과\"),\n" +
            "    NORTHEAST_ASIA_COMMERCE(\"동북아통상학과\"),\n" +
            "    URBAN_PLANNING_POLICY(\"도시계획·정책학과(협동과정)\"),\n" +
            "    EARLY_CHILDHOOD_FOREST_NATURE_EDUCATION(\"유아·숲·자연교육학과(협동과정)\"),\n" +
            "    TOURISM_CONVENTION_ENTERTAINMENT(\"관광컨벤션엔터테인먼트학과\"),\n" +
            "\n" +
            "    // 자연과학계열\n" +
            "    LIFE_SCIENCE_DEPARTMENT(\"생명과학과\"),\n" +
            "    CLOTHING_TEXTILES(\"의류학과\"),\n" +
            "    BEAUTY_INDUSTRY(\"뷰티산업학과\"),\n" +
            "\n" +
            "    // 공학계열\n" +
            "    COMPUTER_ENGINEERING_DEPARTMENT(\"컴퓨터공학과\"),\n" +
            "    CONSTRUCTION_ENVIRONMENTAL_ENGINEERING_DEPARTMENT(\"건설환경공학과\"),\n" +
            "    ENVIRONMENTAL_ENERGY_ENGINEERING(\"환경에너지공학과\"),\n" +
            "    URBAN_CONSTRUCTION_ENGINEERING(\"도시건설공학과\"),\n" +
            "    ARCHITECTURE(\"건축학과\"),\n" +
            "    LIFE_NANO_BIOTECHNOLOGY(\"생명·나노바이오공학과\"),\n" +
            "    CLIMATE_INTERNATIONAL_COOPERATION(\"기후국제협력학과(협동과정)\"),\n" +
            "    URBAN_CONVERGENCE_COMPLEX(\"도시융ㆍ복합학과(협동과정)\"),\n" +
            "    INTELLIGENT_SEMICONDUCTOR_ENGINEERING(\"지능형반도체공학과(협동과정)\"),\n" +
            "    ARTIFICIAL_INTELLIGENCE(\"인공지능학과(협동과정)\"),\n" +
            "    FUTURE_MOBILITY(\"미래모빌리티학과(협동과정)\"),\n" +
            "    BIO_HEALTH_CONVERGENCE(\"바이오헬스융합학과(협동과정)\"),\n" +
            "\n" +
            "    // 예술체육계열\n" +
            "    PHYSICAL_EDUCATION_DEPARTMENT(\"체육학과\"),\n" +
            "    FINE_ARTS(\"미술학과\"),\n" +
            "    DESIGN_DEPARTMENT(\"디자인학과\"),\n" +
            "\n" +
            "\n" +
            "\n" +
            "    //동북아 물류 대학원\n" +
            "    LOGISTICS_MANAGEMENT(\"물류경영학과\"),\n" +
            "    LOGISTICS_SYSTEM(\"융합물류시스템학과\"),\n" +
            "\n" +
            "    //교육 대학원\n" +
            "    EDUCATIONAL_ADMINISTRATION_LEADERSHIP(\"교육행정·리더십전공\"),\n" +
            "    INSTRUCTIONAL_DESIGN_CONSULTING(\"수업설계·수업컨설팅 전공\"),\n" +
            "    LIFELONG_VOCATIONAL_EDUCATION(\"평생·직업교육전공\"),\n" +
            "    COUNSELING_PSYCHOLOGY(\"상담심리전공\"),\n" +
            "    CREATIVITY_GIFTED_EDUCATION(\"창의성·영재교육전공\"),\n" +
            "    CHILD_ART_PSYCHOTHERAPY(\"아동 예술심리치료전공\"),\n" +
            "    MEDIA_EDUCATION(\"미디어교육전공\"),\n" +
            "    MECHANICAL_EDUCATION(\"기계교육전공\"),\n" +
            "    ART_EDUCATION(\"미술교육전공\"),\n" +
            "    SPORTS_CULTURE_ADMINISTRATION(\"스포츠문화행정전공\"),\n" +
            "\n" +
            "    //정책 대학원\n" +
            "    JUDICIAL_ADMINISTRATION(\"사법행정학과\"),\n" +
            "    CRISIS_MANAGEMENT(\"위기관리학과\"),\n" +
            "    LEGISLATIVE_SECURITY_STUDIES(\"의회정치·안보정책학과\"),\n" +
            "\n" +
            "    //정보 기술 대학원\n" +
            "\n" +
            "    //경영 대학원\n" +
            "\n" +
            "    //공학 대학원\n" +
            "    URBAN_ENGINEERING_MAJOR(\"도시공학전공\"),\n" +
            "    SAFETY_ENVIRONMENTAL_SYSTEM_ENGINEERING(\"안전환경시스템공학전공\"),\n" +
            "    CONVERGENCE_DESIGN(\"융합디자인전공\"),\n" +
            "    ARCHITECTURAL_DESIGN_ENGINEERING(\"건축학전공\"),\n" +
            "\n" +
            "    //문화 대학원\n" +
            "    LOCAL_CULTURE(\"지역문화학과\")")
    @NotBlank(message = "전공을 입력해주세요.")
    private String major;

    @Schema(description = "닉네임", example = "밥줘배고파")
    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @Schema(description = "개인정보 약관 동의", example = "true")
    @NotNull(message = "개인정보 수집 및 이용 동의는 필수입니다.")
    private boolean personalInfoCollectionAgreement;

    @Schema(description = "마케팅 및 광고 활용 동의", example = "false")
    @NotNull(message = "마케팅 및 광고 활용 동의 여부를 입력하세요.")
    private boolean marketingAdvertisement;

    @Schema(description = "재학생 인증 여부", example = "true")
    @NotNull(message = "재학생 인증을 진행하세요.")
    private boolean studentVerification;
}
