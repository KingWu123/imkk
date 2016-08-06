# imkk
create simple IM  socket sample

###1. 包com.imkk.socketcs 下面实现了一个TCP通信。
	
	Class Message是自定义的协议对象，协议由 包头标记位 + 消息类型 + 包体长度 + 包体 组成。
	
	Class MessageStream用于将Socket InputStream流的内容缓存下来, 进行"半包"和"黏包"的处理, 最终返回完整的 Message包信息。处理的过程在内存里， 所以, **对于大型的文件,并不适用**。
	
	Class SocketClient实现了一个客户端Tcp socket通信
	
	Class SocketServer实现了一个服务器Tcp Socket通信
	
	FileUtil用于文件的发送与接收。
	
		默认情况下，服务器是接受比较短的普通消息。当需要传输文件时，客户端先发送一条普通的消息，告知服务器，需要发送文件了，服务器收到这条消息后，会切换到文件的接收处理。当服务器接受完客户端发送的文件后，会切换回普通消息的接收处理。
	
	
###2. 包com.imkk.noCenterTalk 下实现了一个UDP无中心的聊天系统

	 实现过程如下:
	  1. 每个User会用 {@see BroadcastService}创建一个广播MulticastSocket, 使用这个广播socket将自身的用户信息(包括ip/port等)广播出去。
	  
	  2. 当局域网内 加入组播的用户收到这个广播通知后,就在自己的用户表里记录下收到的用户信息。同时向这个用户的ip/port发送一条自身信息 (注意:这个过程不是广播,而是单播通信)
	  
      3. 这样每个用户就能维护一个局域网所有用户的信息表,知道对方的ip/port。 可以有选择的进行tcp/udp通信了。当用户的上线状态改变时,也用此流程处理
      
      **上面的方案类似于ARP的处理方式**
      
      待完成工功能 :
      a)当数据较多时，需要对UDP进行分包处理（一个UDP包 < 1500 bytes）,分包后的重组问题没有解决。
      b)如何保证UDP通信的可靠性）
      
###3. 