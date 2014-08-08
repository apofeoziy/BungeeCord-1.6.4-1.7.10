package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;

public class ClientStatusRewriter extends PacketRewriter
{

    public static byte[] statuses = new byte[ 5 ];
    static
    {
        statuses[ 0 ] = 1;
    }

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        byte status = in.readByte();
        out.writeByte( statuses[ status ] );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        unsupported( false );
    }
}
