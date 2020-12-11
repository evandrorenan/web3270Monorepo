import axios                from 'axios';

export const newSessionHttpRequest = (newSessionReturnHandler) => {
    console.log("newSessionHttpRequest: " + newSessionReturnHandler)
    const body = {
        "host": "192.168.240.1",
        "port": "51004"
    }

    axios.post ("http://localhost:8080/newSession", body, { crossdomain: true })
         .then ( response => { 
            console.log("newSessionHttpRequest: " + response.data);
            newSessionReturnHandler(response);
         })
        
}

export const getScreen = (sessionId, ) => {
    axios.get ("http://localhost:8080/session/" + sessionId + "/Screen", { crossdomain: true })
         .then ( response => {
            this.handleScreen(response);
   });
}


// export const postUserInput = (body) => {
//     axios.post ("http://localhost:8080/userInput", body, { crossdomain: true })
//          .then ( response => {
//             console.log(response);
//          });
// }


    // axios.get ("http://localhost:8080/userInput", { crossdomain: true })
    // .then( response => {
    //        console.log(response);
    //        let localState = {};
    //        localState.fields    = response.data.fields;
    //        localState.sessionId = response.data.sessionId;
    //        localState.rows      = mountRows(response.data.fields);
    //        this.setState(localState);
    // });
// }
