using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Http;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Linkpoint.LLSD.Network
{
    public class EventQueueEvent
    {
        public string EventName { get; set; } = string.Empty;
        public LLSDValue Body { get; set; } = LLSDValue.Undefined;
    }

    /// <summary>
    /// Helper client for Second Life capability requests and EventQueueGet polling.
    /// </summary>
    public class CapabilityClient
    {
        private readonly HttpClient _httpClient;

        public CapabilityClient(HttpClient? httpClient = null)
        {
            _httpClient = httpClient ?? new HttpClient();
        }

        /// <summary>
        /// Request seed capability dictionary or request individual capabilities.
        /// </summary>
        public async Task<LLSDValue> PostLLSDAsync(string capabilityUrl, LLSDValue payload, CancellationToken cancellationToken = default)
        {
            if (string.IsNullOrWhiteSpace(capabilityUrl))
            {
                throw new ArgumentNullException(nameof(capabilityUrl));
            }

            string xmlPayload = LLSDSerialize.ToXML(payload, withDeclaration: true);
            using var content = new StringContent(xmlPayload, Encoding.UTF8, "application/llsd+xml");

            using var response = await _httpClient.PostAsync(capabilityUrl, content, cancellationToken).ConfigureAwait(false);
            response.EnsureSuccessStatusCode();

            byte[] responseBytes = await response.Content.ReadAsByteArrayAsync().ConfigureAwait(false);
            return LLSDSerialize.Parse(responseBytes);
        }

        /// <summary>
        /// Polls EventQueueGet capability and parses array of events.
        /// </summary>
        public async Task<List<EventQueueEvent>> PollEventQueueAsync(string eventQueueUrl, int ack = 0, CancellationToken cancellationToken = default)
        {
            var reqMap = new Dictionary<string, LLSDValue>
            {
                ["ack"] = LLSDValue.FromInteger(ack),
                ["done"] = LLSDValue.FromBoolean(false)
            };

            LLSDValue responseLlsd = await PostLLSDAsync(eventQueueUrl, LLSDValue.FromMap(reqMap), cancellationToken).ConfigureAwait(false);

            var events = new List<EventQueueEvent>();
            if (responseLlsd.IsMap && responseLlsd.Has("events"))
            {
                LLSDValue eventsArray = responseLlsd["events"];
                foreach (LLSDValue evtItem in eventsArray.AsArray())
                {
                    if (evtItem.IsMap)
                    {
                        events.Add(new EventQueueEvent
                        {
                            EventName = evtItem["message"].AsString(),
                            Body = evtItem["body"]
                        });
                    }
                }
            }

            return events;
        }
    }
}
