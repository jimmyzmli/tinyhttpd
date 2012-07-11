
package jserver.config;

import jserver.exception.*;

import java.lang.reflect.*;

/**
 *
 * @author Jimmy
 */
public enum DirectiveType {
    LoadModule( LoadModuleDir.class ),
    ServerRoot( SetRootDir.class ),
    Listen( SetListenPortDir.class ),
    DirectoryIndex( DefaultPageDir.class ),
    TypesConfig( SetMIMEDir.class ),
    AddType( AddTypeDir.class ),
    AddHandler( HandlerExtendDir.class ),
    AliasPort( AliasPortDir.class )
    ;

    private static final String DEFAULT_METHOD = "invoke";

    private Method directive;
    private String directiveName;

    DirectiveType( Class funcCls ){
        //Use the default String as the directive name.
        init( funcCls, DEFAULT_METHOD, super.toString() );
    }
    DirectiveType( Class funcCls, String dirName ){
        init( funcCls, DEFAULT_METHOD, dirName );
    }
    DirectiveType( Class funcCls, String methodName, String dirName ){
        init( funcCls, methodName, dirName );
    }
    
    private void init( Class funcCls, String methodName, String dirName ){
        //Assignment
        directiveName = dirName;
        //Get the processing function.
        try{
            directive = funcCls.getMethod( methodName, Configeration.class, String.class ) ;
        }catch( Exception e ){
            throw new SimpleMessage( "Failed to find the proper data in directive declaration file ("+dirName+") : "+e.getMessage() );
        }
    }

    //Accessors.
    public void invoke( Configeration config, String paramStr ){
        try{
            //The processor method is static. (Or should be)
            directive.invoke( null, config, paramStr );
        }catch( ServerException e ){
            //Look for a server exception.
            throw e;
        }catch(Exception e ){
            if( e.getCause() instanceof ServerException )
                e.getCause().printStackTrace();
            e.getCause().printStackTrace();
            throw new SimpleMessage( "Failed to invoke directive method : "+e.getMessage() );
        }
    }
    @Override public String toString(){ return directiveName; }
}
