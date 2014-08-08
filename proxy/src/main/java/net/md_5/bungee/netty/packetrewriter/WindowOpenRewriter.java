package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.Var;

public class WindowOpenRewriter extends PacketRewriter {

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        unsupported( true );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        out.writeBytes( in.readBytes( 2 ) );
        Var.writeString( Var.readString( in, false ), out, true );
        out.writeBytes( in.readBytes( in.readableBytes() ) );
    }

}
