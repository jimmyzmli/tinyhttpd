	private class ConnectionHandler implements Runnable{
		/* Takes in a connection and processes it as nessary, this class does not provide all implementation details but
		* process the request with other diferent handlers. This class have no special interfaces.*/

		private Connection connection;
		private Thread runner;
		//Used as a signal to stop and start processing.
		private final Object signal = new Object();
		private volatile boolean running = false;

		ConnectionHandler(){
			//Create without explicitly starting.
		}
		ConnectionHandler( Connection cnt ){
			handle( cnt );
		}
		ConnectionHandler( Socket cnt, ConnectionType type ){
			handle( cnt, type );
		}

		public void handle( Connection cnt ){
			//Assignment.
			connection = cnt;
			//Start the processor.
			start();
		}
		public void handle( Socket cnt, ConnectionType type ){
			//Create the connection.
			connection = new Connection( cnt, type );
			//Uses a new thread to process the request, so that it doesn't slow anything down.
			start();
		}

		public void start(){

			//If the handler is busy, then ignore the request.
			if( isRunning() ) return;
			//If the thread is already started, then just invoke the thread.
			if(runner==null){
				//Create the thread.
				runner = new Thread( this );
				runner.start();
			}else{
				//Awake the thread.
				synchronized( signal ){
					signal.notifyAll();
				}
			}
			setRunning( true );
		}

		public boolean isRunning(){ return running; }
		public void setRunning( boolean isRunning ){
			//Sets the status of weather the handler is in use.
			if( ! isRunning ){
				//Set the status also for the listing.
				handlerList.checkIn(this);
			}
			running = isRunning;
		}

		//The method start interface

		public synchronized void run(){

			while( true ){
				/* Processes the request. */
				//Create a handler to handle all the user INPUTs.
				InputHandler handler = InputHandler.createHandler( connection );
				try{
					//For responding.
					OutputStream out = connection.getOutputStream();
					while( handler.keepAlive() ){

						//Get the next request string.
						String requestStr = handler.nextRequestString();

						//Process the request string into properties.
						Request request = RequestParser.parse( requestStr, connection.getRequestType() );

						//Get the specified data.
						Data data = request.getRequestedData();

						//Process the data with any modules needed.
						ModManager.process( request, data );

						//Create a header for the requested data given.
						Header header = Header.create( request, new DataDescription(data) );

						//Write the header.
						out.write( header.getBytes() );
						//Send out the header so the data can be started sending.
						out.flush();
						//Write the data.
						out.write( data.getBytes() );
						out.flush();

					}
					//Close the connection if processing is done.
					connection.close();

				}catch( SocketException e){
					//If the problem is the socket, then there's no way to keep this connection.
					//Thus end the connection.
					if( !connection.getLink().isClosed() )
					connection.close();

				}catch(IOException e ){
					Err.report( "IO Error in processing request. "+e.getMessage() );
				}catch( ServerException e ){
					//If the exception is a server one, then just report it.
					e.printStackTrace();
				}catch( RuntimeException e ){
					//Let no exceptions escape, otherwise, this thread will exit. (When this method returns)
					Err.report(e.getMessage());
				}

				//After processing the given connection, wait for the next one.
				//First, set the status.
				setRunning( false );

				//Then pause and wait.
				try{
					do{
						synchronized( signal ){
							signal.wait();
						}
						//Make sure the current connection is open and exsists.
					}while( connection == null || connection.getLink().isClosed());
					//Go back into wait if the connection is bad.
				}catch(InterruptedException e){
					//If the user wants to stop this thread, then do.
					return;
				}
				//Once awaken, start processing the current connection.

			}
		}

	}