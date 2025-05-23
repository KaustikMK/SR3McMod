{
  "schemaVersion": 1,
  "id": "sr3particles",
  "version": "1.0.0",
  "name": "SR3 Particle Projection",
  "description": "Projects a Saints Row 3 boss hologram in Minecraft using live particle data.",
  "authors": ["YourName"],
  "contact": {
    "sources": "https://github.com/YourRepoHere"
  },
  "license": "MIT",
  "environment": "*",
  "entrypoints": {
    "client": [
      "com.project.sr3particles.SR3ParticleProjection"
    ]
  },
  "depends": {
    "fabricloader": ">=0.14.0",
    "minecraft": "1.19.4",
    "fabric-api": "*"
  }
}