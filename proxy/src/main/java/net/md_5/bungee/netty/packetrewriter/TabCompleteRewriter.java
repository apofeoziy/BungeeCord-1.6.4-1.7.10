package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.Var;

public class TabCompleteRewriter extends PacketRewriter
{

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        String cursor = Var.readString( in, true );
        Var.writeString( cursor, out, false );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        String[] commands = Var.readString( in, false ).split( "\0" );
        Var.writeVarInt( commands.length, out );
        for ( String command : commands ) {
            Var.writeString( command, out, true );
        }
    }

}
