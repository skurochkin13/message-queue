This project is an example of possible solution of issue about concurrent using of single messages queue by three types of threads.
type#1: Producer thread(s) that places messages to queue;
type#2: Inspector thread that is responsible for verifying and updating if needed messages in queue;
type#3: Consumer thread gets messages from queue.
Note: Consumer should get messages after their processing by Inspector only.
