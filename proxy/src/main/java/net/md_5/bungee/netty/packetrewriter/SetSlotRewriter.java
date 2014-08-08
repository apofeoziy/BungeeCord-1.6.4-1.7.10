package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.Var;

public class SetSlotRewriter extends PacketRewriter
{
    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        unsupported( true );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        out.writeBytes( in.readBytes( 3 ) ); // byte, short
        Var.rewriteItemData( in, out );
    }
}
