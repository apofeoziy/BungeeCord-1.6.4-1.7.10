package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.Var;

public class EncryptionResponseRewriter extends PacketRewriter
{

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        out.writeBytes( in.readBytes( in.readableBytes() ) );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        String UUID = Var.readString( in, false );
        String username = Var.readString( in, false );
        Var.writeString( UUID, out, true );
        Var.writeString( username, out, true );
    }

}
