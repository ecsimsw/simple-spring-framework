# MVC 프레임워크 구현하기
직접 simple spring framework를 만들어보고, 사용해보기

[DispatcherServlet](https://github.com/ecsimsw/simple-spring-framework/blob/step2/mvc/src/main/java/nextstep/mvc/DispatcherServlet.java)   
[HandlerExecution](https://github.com/ecsimsw/simple-spring-framework/blob/step2/mvc/src/main/java/nextstep/mvc/handler/mapping/HandlerExecution.java)    
[AnnotationHandlerMapping](https://github.com/ecsimsw/simple-spring-framework/blob/step2/mvc/src/main/java/nextstep/mvc/handler/mapping/AnnotationHandlerMapping.java)    
[ArgumentResolver](https://github.com/ecsimsw/simple-spring-framework/blob/step2/mvc/src/main/java/nextstep/mvc/handler/param/ArgumentResolver.java)    

</br>

### mvc 모듈 - 프레임워크 구현부
``` 
Assembler : 객체 초기화   
HandlerMappings : List<HandlerMapping>, handler.initialize()    
AnnotationHandlerMapping : @controller, @RequestMapping 탐색, 핸들러 매핑   
HandlerAdapters : HandlerAdapter 탐색, 실행    
HandlerExecutionAdapter : HandlerExecution 매핑, 실행   
HandlerExecution : 요청 처리 Handler 호출   
ArgumentResolver : @RequestParam, @httpsession, HttpServletRequest, HttpServletResponse 인자 매핑   
VeiwResolver : 뷰 객체 저장 또는 생성   
ExceptionHandlerExecuter : @ExecptionHandler 매핑   
ExceptionHandlerExecution : 예외 처리 Handler 호출   
ViewResolver : JspViewResolver, JsonViewResolver, RedirectionViewResolver로 분리   
JsonModelMapper : Model 값을 json으로 변환   
```

</br>

### app 모듈 - 프레임워크 사용부 
```
@Controller
@RequestMapping  
@ExceptionHandler  
@RequestParam
```  

</br>

### 미션 후기 / 리뷰

정말 재밌게 미션했습니다. 정말 신기했어요. 점점 스프링을 다루듯 App 모듈이 변하는게.
이전 http 미션과 큰 구조는 같도록 하되, 이번에는 프레임워크와 명확하게 서비스 모듈을 분리하고자 노력했어요.

특이 사항이 있다면, @componentscan과 @component를 만들고 컨테이너에 빈들 띄우는 것까지는 구현이 되었는데 주입을 못하고 있어요.
예를 들면 AnnotationHandlerMapping에서 Assembler를 의존하고 있지 않고, Assembler가 static 으로 구현되어 있는 상황이 아닌 상태에서 어떻게 동적으로 주입해야할지 고민 중이에요. 구조가 좀 바뀌어야할 것 같은데, 제가 주말까지 다른 할일이 쌓여서 일단 IOC 구현 멈추고 주말 이후에 다시 작업해보려고요.
혼자 더 고민해보고 싶어서 이 의존성 주입 관련 피드백은 안주셔도 될 것 같습니다.

아 그리고 마음에 걸리는게 하나 더 있었는데, 지금 ExceptionHandler도 Handler로 구현해서, HandlerMapping, HandlerAdpator로 합칠 수 있었는데 굳이 ExceptionHandlerExecuter로 따로 두는 것이 좋을까 아닐까를 많이 고민했습니다.
결과적으로는 요청 처리 핸들러를 다루는 컴포넌트와 에러 처리 핸들러를 다루는 컴포넌트를 아예 분리하는 것이 오히려 명확하겠다, 사용하기 쉽겠다는 생각에 분리하게 되었습니다.

사용자가 편해야 한다는 지난 번 너잘형의 피드백이 생각났네요. 
요청 처리 핸들러와 에러 처리 핸들러를 추상화하면 코드 중복은 피할 수 있었겠지만, Object 인자 남발에 사용자가 오히려 헷갈릴거라는 생각을 했어요.
