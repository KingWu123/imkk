# imkk
create simple IM  socket sample

###1. 包com.imkk.udpSocketTalk 下面实现了一个TCP通信。
	
Class Message: 自定义的协议对象，协议由 包头标记位 + 消息类型 + 包体长度 + 包体 组成。
	
Class MessageStream: 用于将Socket InputStream流的内容缓存下来, 进行"半包"和"黏包"的处理, 最终返回完整的 Message包信息。处理的过程在内存里， 所以,**对于大型的文件,并不适用**。
	
Class SocketClient: 实现了一个客户端Tcp socket通信
	
Class SocketServer: 实现了一个服务器Tcp Socket通信
	
FileUtil用于文件的发送与接收。
	
默认情况下，服务器是接受比较短的普通消息。当需要传输文件时，客户端先发送一条普通的消息，告知服务器，需要发送文件了，服务器收到这条消息后，会切换到文件的接收处理。当服务器接受完客户端发送的文件后，会切换回普通消息的接收处理。
	
	
###2. 包com.imkk.udpSocketTalk 下实现了一个UDP无中心的聊天系统	
实现过程如下:		
	
1. 每个User会用 {@see BroadcastService}创建一个广播MulticastSocket, 使用这个广播socket将自身的用户信息(包括udp的ip/port等)广播出去。		

2. 当局域网内 加入组播的用户收到这个广播通知后,就在自己的用户表里记录下收到的用户信息。同时向这个用户的ip/port发送一条自身信息 (注意:这个过程不是广播,而是单播通信)。	
	
3.   这样每个用户就能维护一个局域网所有用户的信息表,知道对方的ip/port。 可以有选择的进行tcp/udp通信了。当用户的上线状态改变时,也用此流程处理
      
**上面的方案类似于ARP的处理方式**

<font color="red">   
待完成工功能 :	
a)当数据较多时，需要对UDP进行分包处理（一个UDP包 < 1500 bytes）,分包后的重组问题没有解决。	
b)如何保证UDP通信的可靠性）
</font> 

UDP的可靠一般有两种处理方案: 一种是，在UDP上自定义一层可靠性协议，其实现原理类似于TCP的方式；另一种是，发送方发送完后，设置一个定时器，在定时器时间内，收到对方的确认消息，即为发送成功，否则重发。

这里为了保证聊天通信的可靠性，可以使用UDP广播自身信息，TCP单点通信的方式，思路如下:

1. UDP广播之前，先建立一个TCP ServerSocket，然后将ServerSocket的ip/port等个人信息广播出去。	
2. 广播的接收方接收到这个信息后， 缓存下来这个用户信息，然后创建一个client Tcp socket，与广播方的SeverSocket建立连接。通过这个socket连接，接收方告知广播方自己的个人信息（信息主要是ServerSocket ip/port）。 通过以上两步，每个用户都可以保留其他所有用户的的个人信息（主要是对方的ServerSocket ip/port)	
3.  当需要进行聊天时，都由发起方先创建一个tcp scoket，连接对方的serverSocket，聊天结束时，socket断开。
 
简单概括为： 每个user创建一个ServerSocket，通过广播数据 交换得到所有人的ServerSocket.进行聊天时，发起方去链接对方的ServerSocket即可。这样就保证了通信的可靠性。
      
###3. 包com.imkk.noCenterTalk 下实现了一个无中心的聊天系统

1. package com.imkk.noCenterTalk.channelLayer  通信层，提供tcp、udp socket通信

111

2. package com.imkk.noCenterTalk.businessLayer 业务逻辑层， 提供数据的发送、接受等功能

2222

3.  package com.imkk.noCenterTalk.presentationLayer 表示层， 提供UI，用于用户聊天

###3. 实现一个有中心的聊天系统
