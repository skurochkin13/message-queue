This project is an example of possible solution of issue about concurrent using of single messages queue by three types of threads.  
_type#1:_ Producer thread(s) that places messages to queue;  
_type#2:_ Inspector thread that is responsible for verifying and updating if needed messages in queue;  
_type#3:_ Consumer thread gets messages from queue.  
_Note:_ Consumer should get messages after their processing by Inspector only.
