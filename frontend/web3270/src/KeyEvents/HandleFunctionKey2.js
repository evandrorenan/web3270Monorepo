import { getPosition   } from "./InputFieldInfo"

const HandleFunctionKey2 = (event, position, screenState) => {
//         let request = {};

//         screenState: {…}
//         ​
//         cursorPos: 0
//         ​
//         fieldPos: (3) […]
//         ​​
//         0: 1
//         ​​
//         1: 1868
//         ​​
//         2: 1919
//         ​​
//         length: 3
//         ​​
//         <prototype>: []
//         ​
//         positions: (1920) […]
//         ​
//         sessionId: "2020-09-24 21:31:57.305"        
// ;
//         let newContent = [];

//         screenState.positions;
        for ()

        for (let i = 0; i < 1920; i++) {
            if (positions[i])

            if (!positions[i].protected) {
                if (!positions[i - 1].protected) {
                    newContent[index] += 
                }
            }
        }

        request.sessionId = screenState.sessionId;
        let functionKey = setPfkey(event);
        if (functionKey === "") {
            return "";
        }
        
        const screen = event.target.parentNode.parentNode;

        for (let row = 0; row < 24; row++){
            for (let col = 0; col < 80; col++) {
                for (let fieldInd = 0; fieldInd < screenState.fields.length; fieldInd++) {
                    let fieldStart = ( screenState.fields[fieldInd].startRow    - 1 ) * 80
                                     + screenState.fields[fieldInd].startColumn - 1;
                    let position = getPosition(screen.children[row].children[[col]]);
                    if (position >= fieldStart 
                    &&  position <= fieldStart + screenState.fields[fieldInd].length){
                        screenState.fields[fieldInd].text = 
                            screenState.fields[fieldInd].text.substring(0, position - fieldStart)
                        +   screen.children[row].children[[col]].value
                        +   screenState.fields[fieldInd].text.substring(position - fieldStart + 1);
                    }
                }
            }
        }

        request.functionKey = functionKey;
        request.fields = screenState.fields;
        return JSON.parse(JSON.stringify(request).replaceAll("protected", "isProtected")
                                      .replaceAll("hidden"   , "isHidden")
                                      .replaceAll("highLight", "isHighLight"));
}

const setPfkey = (event) => {

    const keyInputs         = ["F1"    , "F2"      , "F3"     , "F4"     , "F5"    , "F6"    , 
                               "F7"    , "F8"      , "F9"     , "F10"    , "F11"   , "F12"   , 
                               "PageUp", "PageDown", "Enter"  , "Pause"  ];

    const functionKeys      = ["[pf1]" , "[pf2]"   , "[pf3]"  , "[pf4]"  , "[pf5]" , "[pf6]" ,  
                               "[pf7]" , "[pf8]"   , "[pf9]"  , "[pf10]" , "[pf11]", "[pf12]",
                               "[pa1]" , "[pa2]"   , "[enter]", "[clear]"];

    const shiftFunctionKeys = ["[pf13]", "[pf14]"  , "[pf15]" , "[pf16]" , "[pf17]", "[pf18]", 
                               "[pf19]", "[pf20]"  , "[pf21]" , "[pf22]" , "[pf23]", "[pf24]",
                               "[pa3]"]
    
    let index = keyInputs.indexOf(event.key); 
    if (event.shiftKey) {
       if (index <= 12 ) {
           return shiftFunctionKeys[index];
       } else {
           return "";
       }
    } else {
        return functionKeys[index];
    }
}

export default HandleFunctionKey2;