       //Get the last few bytes of the processing data.
       byte[] oldText = data.getBytes();
       String ending = new String( java.util.Arrays.copyOfRange( oldText, oldText.length - SAMPLE_AMT, oldText.length ) );
       //Scan the result for the ending string.
       Scanner scn = new Scanner( result );
       scn.useDelimiter(ending);
       //Now read in.
       data.reset( scn.next() );