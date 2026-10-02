import { GoogleGenAI } from "@google/genai";

export const ai = new GoogleGenAI({
  apiKey: process.env.GEMINI_API_KEY,
  httpOptions: {
    headers: {
      'User-Agent': 'aistudio-build',
    }
  }
});

// Developer tool: explains pasted LLSD. It never produces grid, resident, chat
// or inventory data; the viewer only ever shows what a live grid supplies.
export async function processLLSDWithGemini(llsdData: string, task: string): Promise<string> {
  if (!process.env.GEMINI_API_KEY) {
    return `Gemini API key is not configured. Input received: ${llsdData.slice(0, 100)}...`;
  }
  const response = await ai.models.generateContent({
    model: "gemini-3.8-flash",
    contents: `You are an expert on Linden Lab Structured Data (LLSD) and the Second Life protocol.
Task: ${task}
LLSD Data:
${llsdData}`,
  });
  return response.text || "No response generated";
}
