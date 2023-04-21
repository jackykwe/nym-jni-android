# Adapted from nym/clients/native/examples/python-examples/websocket/textsend.py

import asyncio
import json

import aioconsole
import websockets

self_address_request = json.dumps({
    "type": "selfAddress"
})

RECIPIENT = "ECiRjCAyCfhS8zyoHGvEtSxW3v6r43r3TrW8PfY3YULn.DLjhDuekdENHq8Mpdp355UH94usQtpZNjHK2ibPGf4BU@GL5wESoz4oSbpBaTki9qB9213FGUQXCiRjbzDkhWwoBC"

# Adapted from send_text_without_reply()
async def read_from_stdin_and_send_text_without_reply(websocket):
    await websocket.send(self_address_request)
    self_address = json.loads(await websocket.recv())
    print("our address is: {}".format(self_address["address"]))
    while True:
        message = await aioconsole.ainput("Enter message to send to contact: ")
        text_send = json.dumps({
            "type": "send",
            "message": f"{self_address['address']}|{message}",
            "recipient": RECIPIENT,
            "withReplySurb": False,
        })
        print()
        print(f"sending '{message}' to {RECIPIENT}")
        print()
        await websocket.send(text_send)



# Adapted from send_text_without_reply()
async def receive_and_print_messages_from_mixnet(websocket):
    while True:
        backoff = 1
        while True:
            try:
                received_message = await websocket.recv()
                received_message = json.loads(received_message)["message"]
                separator_index = received_message.find("|")
                sender_nym_address = received_message[:separator_index]
                payload = received_message[separator_index + 1:]
                break
            except RuntimeError as e:
                print(f"Silencing exception: {e}")
                print(f"The other coroutine is awaiting on recv(). This coroutine is backing off for {backoff}s...")
                await asyncio.sleep(backoff)
                backoff *= 2
                continue
            except Exception as e:
                print(f"Unexpected exception: {e}")
        print()
        print()
        print(f"received '{payload}' from {sender_nym_address}")
        print()
        print("Enter message to send to contact: ", end="", flush=True)



# async def send_text_with_reply():
#     message = "Hello Nym!"

#     uri = "ws://localhost:1977"
#     async with websockets.connect(uri) as websocket:
#         await websocket.send(self_address_request)
#         self_address = json.loads(await websocket.recv())
#         print("our address is: {}".format(self_address["address"]))

#         text_send = json.dumps({
#             "type": "send",
#             "message": message,
#             "recipient": self_address["address"],
#             "withReplySurb": True,
#         })

#         print("sending '{}' (*with* reply SURB) over the mix network...".format(message))
#         await websocket.send(text_send)

#         print("waiting to receive a message from the mix network...")
#         received_message = json.loads(await websocket.recv())
#         print("received '{}' from the mix network".format(received_message))

#         # use the received surb to send an anonymous reply!
#         reply_surb = received_message["replySurb"]

#         reply_message = "hello from reply SURB!"
#         reply = json.dumps({
#             "type": "reply",
#             "message": reply_message,
#             "replySurb": reply_surb
#         })

#         print("sending '{}' (using reply SURB!) over the mix network...".format(reply_message))
#         await websocket.send(reply)

#         print("waiting to receive a message from the mix network...")
#         received_message = await websocket.recv()
#         print("received '{}' from the mix network".format(received_message))

async def main():
    uri = "ws://localhost:1977"
    async with websockets.connect(uri) as websocket:
        sender_task = asyncio.create_task(read_from_stdin_and_send_text_without_reply(websocket))
        receiver_task = asyncio.create_task(receive_and_print_messages_from_mixnet(websocket))
        await sender_task
        await receiver_task


asyncio.run(main())
# asyncio.get_event_loop().run_until_complete(send_text_without_reply())
# asyncio.get_event_loop().run_until_complete(send_text_with_reply())


"""
Silencing exception: cannot call recv while another coroutine is already waiting for the next message
The other coroutine is awaiting on recv(). This coroutine is backing off for 1s...
our address is: Huj7FtSiCaE3vwG1zvwYTbks7dpmDY4mwN9pj8x6eMHD.FuTtSKAYqAPJ8P57DaZeVmTzczMSwSVHF63beiqD45jX@9Byd9VAtyYMnbVAcqdoQxJnq76XEg2dbxbiF5Aa5Jj9J
Enter message to send to contact:

received 'Hello PC from Android 👋' from ECiRjCAyCfhS8zyoHGvEtSxW3v6r43r3TrW8PfY3YULn.DLjhDuekdENHq8Mpdp355UH94usQtpZNjHK2ibPGf4BU@GL5wESoz4oSbpBaTki9qB9213FGUQXCiRjbzDkhWwoBC

Enter message to send to contact: Hello Android from PC!

sending 'Hello Android from PC!' to ECiRjCAyCfhS8zyoHGvEtSxW3v6r43r3TrW8PfY3YULn.DLjhDuekdENHq8Mpdp355UH94usQtpZNjHK2ibPGf4BU@GL5wESoz4oSbpBaTki9qB9213FGUQXCiRjbzDkhWwoBC

Enter message to send to contact: Sent when Android client is not running

sending 'Sent when Android client is not running' to ECiRjCAyCfhS8zyoHGvEtSxW3v6r43r3TrW8PfY3YULn.DLjhDuekdENHq8Mpdp355UH94usQtpZNjHK2ibPGf4BU@GL5wESoz4oSbpBaTki9qB9213FGUQXCiRjbzDkhWwoBC

Enter message to send to contact: ^CTraceback (most recent call last):
[...elided]
KeyboardInterrupt
"""
