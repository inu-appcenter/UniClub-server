package com.uniclub.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Major {

    /*
        대학
     */

    // 인문대학
    KOREAN_LANGUAGE_LITERATURE("국어국문학과"),
    ENGLISH_LANGUAGE_LITERATURE("영어영문학과"),
    JAPANESE_REGIONAL_CULTURE("일본지역문화학과"),
    CHINESE_LANGUAGE_LITERATURE("중어중국학과"),
    GERMAN_LANGUAGE_LITERATURE("독어독문학과"),
    FRENCH_LANGUAGE_LITERATURE("불어불문학과"),

    // 자연과학대학
    MATHEMATICS("수학과"),
    PHYSICS("물리학과"),
    CHEMISTRY("화학과"),
    MARINE_SCIENCE("해양학과"),
    FASHION_INDUSTRY("패션산업학과"),

    // 사회과학대학
    SOCIAL_WELFARE("사회복지학과"),
    MEDIA_COMMUNICATION("미디어커뮤니케이션학과"),
    LIBRARY_INFORMATION_SCIENCE("문헌정보학과"),
    CREATIVE_TALENT_DEVELOPMENT("창의인재개발학과"),

    // 글로벌정경대학
    PUBLIC_ADMINISTRATION("행정학과"),
    POLITICAL_SCIENCE_DIPLOMACY("정치외교학과"),
    ECONOMICS("경제학과"),
    TRADE("무역학부"),
    CONSUMER_STUDIES("소비자학과"),

    // 공과대학
    MECHANICAL_ENGINEERING("기계공학과"),
    ELECTRICAL_ENGINEERING("전기공학과"),
    ELECTRONIC_ENGINEERING("전자공학과"),
    INDUSTRIAL_MANAGEMENT_ENGINEERING("산업경영공학과"),
    MATERIALS_SCIENCE_ENGINEERING("신소재공학과"),
    SAFETY_ENGINEERING("안전공학과"),
    ENERGY_CHEMICAL_ENGINEERING("에너지화학공학과"),
    BIO_ROBOT_SYSTEM_ENGINEERING("바이오-로봇 시스템 공학과"),

    // 정보기술대학
    COMPUTER_ENGINEERING("컴퓨터공학부"),
    INFORMATION_COMMUNICATION_ENGINEERING("정보통신공학과"),
    EMBEDDED_SYSTEM_ENGINEERING("임베디드시스템공학과"),

    // 경영대학
    BUSINESS_ADMINISTRATION("경영학부"),
    DATA_SCIENCE("데이터과학과"),
    TAXATION_ACCOUNTING("세무회계학과"),
    TECHNO_MANAGEMENT("테크노경영학과"),

    // 예술체육대학
    KOREAN_PAINTING("한국화전공(조형예술학부)"),
    WESTERN_PAINTING("서양화전공(조형예술학부)"),
    DESIGN("디자인학부"),
    PERFORMING_ARTS("공연예술학과"),
    SPORTS_SCIENCE("스포츠과학부"),
    EXERCISE_HEALTH("운동건강학부"),

    // 사범대학
    KOREAN_EDUCATION("국어교육과"),
    ENGLISH_EDUCATION("영어교육과"),
    JAPANESE_EDUCATION("일어교육과"),
    MATHEMATICS_EDUCATION("수학교육과"),
    PHYSICAL_EDUCATION("체육교육과"),
    EARLY_CHILDHOOD_EDUCATION("유아교육과"),
    HISTORY_EDUCATION("역사교육과"),
    ETHICS_EDUCATION("윤리교육과"),

    // 도시과학대학
    URBAN_ADMINISTRATION("도시행정학과"),
    CONSTRUCTION_ENVIRONMENTAL_ENGINEERING("건설환경공학"),
    ENVIRONMENTAL_ENGINEERING("환경공학"),
    URBAN_ENGINEERING("도시공학과"),
    ARCHITECTURAL_ENGINEERING("건축공학"),
    URBAN_ARCHITECTURE("도시건축학"),

    // 생명과학기술대학
    LIFE_SCIENCE("생명과학부(생명과학전공)"),
    MOLECULAR_LIFE_SCIENCE("생명과학부(분자의생명전공)"),
    BIOTECHNOLOGY("생명공학부(생명공학전공)"),
    NANO_BIOTECHNOLOGY("생명공학부(나노바이오공학전공)"),

    // 융합자유전공대학
    FREE_MAJOR("자유전공학부"),
    INTERNATIONAL_FREE_MAJOR("국제자유전공학부"),
    CONVERGENCE("융합학부"),

    // 동북아국제통상학부
    NORTHEAST_ASIA_INTERNATIONAL_COMMERCE("동북아국제통상전공"),
    SMART_LOGISTICS_ENGINEERING("스마트물류공학전공"),
    IBE("IBE전공"),

    // 법학부 (독립학부)
    LAW("법학부"),


    /*
        대학원
     */

    //일반 대학원

    // 인문사회계열
    KOREAN_LANGUAGE_EDUCATION("한국어교육학과"),
    EDUCATION("교육학과"),
    ETHICS("윤리학과"),
    CHINESE_STUDIES("중국학과"),
    LAW_DEPARTMENT("법학과"),
    BUSINESS_ADMINISTRATION_DEPARTMENT("경영학과"),
    TRADE_DEPARTMENT("무역학과"),
    NORTHEAST_ASIA_COMMERCE("동북아통상학과"),
    URBAN_PLANNING_POLICY("도시계획·정책학과(협동과정)"),
    EARLY_CHILDHOOD_FOREST_NATURE_EDUCATION("유아·숲·자연교육학과(협동과정)"),
    TOURISM_CONVENTION_ENTERTAINMENT("관광컨벤션엔터테인먼트학과"),

    // 자연과학계열
    LIFE_SCIENCE_DEPARTMENT("생명과학과"),
    CLOTHING_TEXTILES("의류학과"),
    BEAUTY_INDUSTRY("뷰티산업학과"),

    // 공학계열
    COMPUTER_ENGINEERING_DEPARTMENT("컴퓨터공학과"),
    CONSTRUCTION_ENVIRONMENTAL_ENGINEERING_DEPARTMENT("건설환경공학과"),
    ENVIRONMENTAL_ENERGY_ENGINEERING("환경에너지공학과"),
    URBAN_CONSTRUCTION_ENGINEERING("도시건설공학과"),
    ARCHITECTURE("건축학과"),
    LIFE_NANO_BIOTECHNOLOGY("생명·나노바이오공학과"),
    CLIMATE_INTERNATIONAL_COOPERATION("기후국제협력학과(협동과정)"),
    URBAN_CONVERGENCE_COMPLEX("도시융ㆍ복합학과(협동과정)"),
    INTELLIGENT_SEMICONDUCTOR_ENGINEERING("지능형반도체공학과(협동과정)"),
    ARTIFICIAL_INTELLIGENCE("인공지능학과(협동과정)"),
    FUTURE_MOBILITY("미래모빌리티학과(협동과정)"),
    BIO_HEALTH_CONVERGENCE("바이오헬스융합학과(협동과정)"),

    // 예술체육계열
    PHYSICAL_EDUCATION_DEPARTMENT("체육학과"),
    FINE_ARTS("미술학과"),
    DESIGN_DEPARTMENT("디자인학과"),



    //동북아 물류 대학원
    LOGISTICS_MANAGEMENT("물류경영학과"),
    LOGISTICS_SYSTEM("융합물류시스템학과"),

    //교육 대학원
    EDUCATIONAL_ADMINISTRATION_LEADERSHIP("교육행정·리더십전공"),
    INSTRUCTIONAL_DESIGN_CONSULTING("수업설계·수업컨설팅 전공"),
    LIFELONG_VOCATIONAL_EDUCATION("평생·직업교육전공"),
    COUNSELING_PSYCHOLOGY("상담심리전공"),
    CREATIVITY_GIFTED_EDUCATION("창의성·영재교육전공"),
    CHILD_ART_PSYCHOTHERAPY("아동 예술심리치료전공"),
    MEDIA_EDUCATION("미디어교육전공"),
    MECHANICAL_EDUCATION("기계교육전공"),
    ART_EDUCATION("미술교육전공"),
    SPORTS_CULTURE_ADMINISTRATION("스포츠문화행정전공"),

    //정책 대학원
    JUDICIAL_ADMINISTRATION("사법행정학과"),
    CRISIS_MANAGEMENT("위기관리학과"),
    LEGISLATIVE_SECURITY_STUDIES("의회정치·안보정책학과"),

    //정보 기술 대학원

    //경영 대학원

    //공학 대학원
    URBAN_ENGINEERING_MAJOR("도시공학전공"),
    SAFETY_ENVIRONMENTAL_SYSTEM_ENGINEERING("안전환경시스템공학전공"),
    CONVERGENCE_DESIGN("융합디자인전공"),
    ARCHITECTURAL_DESIGN_ENGINEERING("건축학전공"),

    //문화 대학원
    LOCAL_CULTURE("지역문화학과");


    private final String majorName;
}
