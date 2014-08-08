package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.Var;

public class EncryptionRequestRewriter extends PacketRewriter
{

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        unsupported( true );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        String serverId = Var.readString( in, false );
        Var.writeString( serverId, out, true );
        out.writeBytes( in.readBytes( in.readableBytes() ) );
    }

}
