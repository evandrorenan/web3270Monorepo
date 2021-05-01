import React, { Component } from 'react';
import SockJsClient from 'react-stomp';
 
class ScreenWebSocket extends Component {
 
  componentDidMount() {
    this.sendMessage(this.props.sessionId);
  }

  sendMessage = (msg, sessionId) => {
    this.clientRef.sendMessage("/subscribe/" + sessionId, msg);
  }
 
  //* TODO: On message, dispatch action
  //        not sure if I really need this ref
  render() {
    return (
      <span>
        <SockJsClient url='http://10.4.66.22:3000/web3270-websocket' topics={['/topic/screens']}
            ref={ (client) => { this.clientRef = client }} />
      </span>
    );
  }
}

export default ScreenWebSocket;