        Pattern search = Pattern.compile("([^#]*)([#].*)?\n");
        //Set the scan text.
        Matcher match = null;
        try{
            match = search.matcher( ServerUtil.getData(fileSrc) );
        }catch(IOException e){
            throw new SimpleMessage("Configeration file failed to be read : "+e.getMessage());
        }
        while( match.find() ){
            //Process(register) each setting.
            //Get the line.
            String setting = match.group(1);
            //Test.d(setting.length() +" : "+setting);
            //Ignore if it's a comment line.
            if( setting.length()==0 ) continue;
            
            Test.d(setting);
        }
        Test.d("CONFIG SET.");