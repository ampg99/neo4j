/*
 * Copyright (c) 2002-2016 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.coreedge.catchup.tx;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import org.neo4j.coreedge.catchup.CatchupClientProtocol;

import static org.neo4j.coreedge.catchup.CatchupClientProtocol.NextMessage;

public class TxStreamFinishedResponseDecoder extends MessageToMessageDecoder<ByteBuf>
{
    private final CatchupClientProtocol protocol;

    public TxStreamFinishedResponseDecoder( CatchupClientProtocol protocol )
    {
        this.protocol = protocol;
    }

    @Override
    protected void decode( ChannelHandlerContext ctx, ByteBuf msg, List<Object> out ) throws Exception
    {
        if ( protocol.isExpecting( NextMessage.TX_STREAM_FINISHED ) )
        {
            long lastTransactionIdSent = msg.readLong();
            boolean success = msg.readBoolean();
            out.add( new TxStreamFinishedResponse( lastTransactionIdSent, success ) );
        }
        else
        {
            out.add( Unpooled.copiedBuffer( msg ) );
        }
    }
}
