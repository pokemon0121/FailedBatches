import java.util.*;


public class RateLimit {

	public static void resetAllowances(int[] users) {
		
	}
	

	public static void main(String[] args) {
		int[][] sentBatches = new int[][] {
			{1471040000, 736273, 827482, 2738283},
			{1471040005, 736273, 2738283},
			{1471040010, 827482, 2738283},
			{1471040015, 2738283},
			{1471040025, 827482},
			{1471046400, 736273, 827482, 2738283}
		};
		
		int[][] receivedMessages = new int[][] {
			{1471040001, 2738283},
		    {1471040002, 2738283},
		    {1471040010, 827482},
		    {1471040020, 2738283}
		};
		
		int startingAllowance = 1;
		// serves as a clock
		int day = 0;
		// initialize limits
		Map<Integer, Integer> limits = new HashMap<>();
		// recording the failure sent batches' indexes
		List<Integer> failureIndexes = new ArrayList<>();
		// two iterators
		int si = 0;
		int ri = 0;
		// begin processing
		while (si < sentBatches.length || ri < receivedMessages.length) {
			// sent time < received time, process sent
			if ((si < sentBatches.length && ri == receivedMessages.length) || (si < sentBatches.length && ri < receivedMessages.length && sentBatches[si][0] < receivedMessages[ri][0])) {
				// if some users in this batch first appear, initialize them
				for (int i = 1; i < sentBatches[si].length; i++) {
					if (limits.get(sentBatches[si][i]) == null) {
						limits.put(sentBatches[si][i], startingAllowance);
					}
				}
				// if messages are sent at or after midnight
				if (sentBatches[si][0] / 86400 > day) {
					// reset is done before sent
					for (int user : limits.keySet()) {
						limits.put(user, startingAllowance);
					}
					// initial limit cannot be 0 so now every message can be sent
					for (int i = 1; i < sentBatches[si].length; i++) {
						limits.put(sentBatches[si][i], limits.get(sentBatches[si][i]) - 1);
					}
					// move forward current days
					day = sentBatches[si][0] / 86400;
				}
				// if messages are sent before midnight
				else {
					// check whether all target users' limits > 0
					// by setting up a flag
					boolean allSentable = true;
					for (int i = 1; i < sentBatches[si].length; i++) {
						if (limits.get(sentBatches[si][i]) <= 0) {
							// we have record and the limit is <= 0, so we cannot send
							allSentable = false;
							// then break the for loop
							// no need to do more checks
							break;
						}
					}
					// decide sent or not by the flag
					if (allSentable) {
						for (int i = 1; i < sentBatches[si].length; i++) {
							limits.put(sentBatches[si][i], limits.get(sentBatches[si][i]) - 1);
						}
					}
					else {
						// this is a failure, add this to result list
						failureIndexes.add(si);
					}
				}
				// ready to deal with next sent batch
				si++;
			}
			// sent time >= received time, process received
			else if ((si == sentBatches.length && ri < receivedMessages.length) || (si < sentBatches.length && ri < receivedMessages.length && sentBatches[si][0] >= receivedMessages[ri][0])){
				// if the user sent this message first appears, initialize him
				if (limits.get(receivedMessages[ri][1]) == null) {
					limits.put(receivedMessages[ri][1], startingAllowance);
				}
				// if received at or after midnight
				if (receivedMessages[ri][0] / 86400 > day) {
					// reset all users first
					for (int user : limits.keySet()) {
						limits.put(user, startingAllowance);
					}
					// then increase the user's limit
					limits.put(receivedMessages[ri][1], limits.get(receivedMessages[ri][1]) + 1);
					// update the day
					day = receivedMessages[ri][0] / 86400;
				}
				// if received before midnight
				else {
					// just increase
					limits.put(receivedMessages[ri][1], limits.get(receivedMessages[ri][1]) + 1);
				}
				// move to next one
				ri++;
			}
			// sent batches ended, received message ongoing
			else if (si == sentBatches.length && ri < receivedMessages.length) {
				// clear the rest received messages
				
			}
		}
		// turn list to array and return
        int[] result = new int[failureIndexes.size()];
        for (int i = 0; i < failureIndexes.size(); i++) {
            result[i] = failureIndexes.get(i);
        }
		System.out.println(failureIndexes);
	}

}
