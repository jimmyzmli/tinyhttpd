
package jserver.ui.command;

/**
 *
 * @author Jimmy
 */
public class HelpCmd {
    public static String invoke( String paramStr ){
        //Format the parameter.
        paramStr = paramStr.trim();

        if( paramStr.contains(" ") )
            return "Invalid parameter. Use \"help <command>\" or \"help\" to show all commands.";

        if( paramStr.length() == 0 ){
            //List all the help.
            String help = "";
            int i = 0;
            for( Command cmd : Command.values() ){
                help += cmd+" ";
                if( (++i)==2 ){
                    help+="\n";
                    i=0;
                }
            }
            return help;
        }else{
            //Get specific help for the given command.
            return "No informatin on that command.";
        }
    }
}
